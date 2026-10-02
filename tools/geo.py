"""Tiny entity model ("geo") toolkit.

A model is a list of bones (Minecraft model parts) with box-UV cubes. This module packs the cubes into a texture,
paints every face procedurally and writes:

* ``assets/dungeons2/geo/<model>.json``     - read by ``GeoLoader`` in the mod to build the ``LayerDefinition``
* ``assets/dungeons2/textures/entity/...``  - one or more textures (+ optional ``_glow`` emissive layers)

Coordinates follow Minecraft's Java model conventions: Y points down, a bone's pivot is relative to its parent,
cube origins are relative to the bone's pivot and the ground is at y = 24.
"""
import json
import math
import random

from PIL import Image

from texlib import clamp8, hex2rgb, out_path

FACE_SHADE = {"up": 1.12, "down": 0.68, "north": 1.0, "south": 0.86, "east": 0.92, "west": 0.92}


class Cube:
    def __init__(self, origin, size, paint, inflate=0.0, mirror=False):
        self.origin = origin
        self.size = tuple(int(s) for s in size)
        self.paint = paint
        self.inflate = inflate
        self.mirror = mirror
        self.uv = None

    def footprint(self):
        w, h, d = self.size
        return 2 * (d + w), d + h

    def faces(self):
        """Face name -> (u, v, width, height) on the texture."""
        u, v = self.uv
        w, h, d = self.size
        return {
            "up": (u + d, v, w, d),
            "down": (u + d + w, v, w, d),
            "east": (u, v + d, d, h),
            "north": (u + d, v + d, w, h),
            "west": (u + d + w, v + d, d, h),
            "south": (u + d + w + d, v + d, w, h),
        }


class Bone:
    def __init__(self, name, parent, pivot, rotation):
        self.name = name
        self.parent = parent
        self.pivot = pivot
        self.rotation = rotation
        self.cubes = []

    def cube(self, origin, size, paint=None, inflate=0.0, mirror=False):
        self.cubes.append(Cube(origin, size, paint or {}, inflate, mirror))
        return self


class Model:
    def __init__(self, name, tex_width=64):
        self.name = name
        self.tex_width = tex_width
        self.tex_height = None
        self.bones = []

    def bone(self, name, pivot=(0, 0, 0), parent=None, rotation=(0, 0, 0)):
        b = Bone(name, parent, pivot, rotation)
        self.bones.append(b)
        return b

    def cubes(self):
        for b in self.bones:
            for c in b.cubes:
                yield c

    def pack(self):
        """Shelf-packs all cubes; computes the texture height (power of two)."""
        items = sorted(self.cubes(), key=lambda c: (-c.footprint()[1], -c.footprint()[0]))
        x = y = shelf_h = 0
        for c in items:
            fw, fh = c.footprint()
            if fw > self.tex_width:
                raise ValueError(f"{self.name}: cube too wide for texture")
            if x + fw > self.tex_width:
                x = 0
                y += shelf_h
                shelf_h = 0
            c.uv = (x, y)
            x += fw
            shelf_h = max(shelf_h, fh)
        needed = y + shelf_h
        h = 16
        while h < needed:
            h *= 2
        self.tex_height = h

    def write_geo(self):
        data = {
            "texture_width": self.tex_width,
            "texture_height": self.tex_height,
            "bones": [],
        }
        for b in self.bones:
            data["bones"].append({
                "name": b.name,
                "parent": b.parent,
                "pivot": list(b.pivot),
                "rotation": list(b.rotation),
                "cubes": [{
                    "origin": list(c.origin),
                    "size": list(c.size),
                    "uv": list(c.uv),
                    "inflate": c.inflate,
                    "mirror": c.mirror,
                } for c in b.cubes],
            })
        path = out_path("geo", self.name + ".json")
        with open(path, "w") as f:
            json.dump(data, f, indent=1)

    def paint(self, texture_name, palette, seed=1):
        """Paints the texture. ``palette`` maps the colour names used in cube paints to hex colours."""
        img = Image.new("RGBA", (self.tex_width, self.tex_height), (0, 0, 0, 0))
        glow = Image.new("RGBA", (self.tex_width, self.tex_height), (0, 0, 0, 0))
        rnd = random.Random(seed)
        for i, c in enumerate(self.cubes()):
            paint_cube(img, glow, c, palette, random.Random(seed * 1000 + i))
        img.save(out_path("textures", "entity", texture_name + ".png"))
        if glow.getbbox() is not None:
            glow.save(out_path("textures", "entity", texture_name + "_glow.png"))
        return img


def resolve(color, palette):
    if color is None:
        return None
    if isinstance(color, tuple):
        return color
    if color.startswith("#"):
        return hex2rgb(color)
    return hex2rgb(palette[color])


def shade(c, f):
    return tuple(clamp8(x * f) for x in c)


def paint_cube(img, glow, cube, palette, rnd):
    p = cube.paint
    base = resolve(p.get("color", "base"), palette)
    accent = resolve(p.get("accent"), palette) if p.get("accent") else shade(base, 0.75)
    pattern = p.get("pattern", "smooth")
    glow_all = p.get("glow", False)
    face_colors = p.get("faces", {})
    for face, (fu, fv, fw, fh) in cube.faces().items():
        if fw <= 0 or fh <= 0:
            continue
        fcol = resolve(face_colors.get(face), palette) or base
        for yy in range(fh):
            for xx in range(fw):
                c = pattern_color(pattern, fcol, accent, xx, yy, fw, fh, face, rnd)
                f = FACE_SHADE[face]
                if p.get("gradient", True) and face not in ("up", "down") and fh > 2:
                    f *= 1.06 - 0.16 * (yy / (fh - 1))
                if fw > 2 and fh > 2 and (xx in (0, fw - 1) or yy in (0, fh - 1)) and p.get("outline", True):
                    f *= 0.88
                f *= 1 + rnd.uniform(-0.05, 0.05)
                col = shade(c, f)
                img.putpixel((fu + xx, fv + yy), col + (255,))
                if glow_all:
                    glow.putpixel((fu + xx, fv + yy), col + (255,))
        # glowing speckles (e.g. sculk spots)
        spots = p.get("glow_spots")
        if spots:
            spot_col = resolve(spots, palette)
            count = max(1, int(fw * fh * p.get("spot_density", 0.06)))
            for _ in range(count):
                x = fu + rnd.randrange(fw)
                y = fv + rnd.randrange(fh)
                col = shade(spot_col, rnd.uniform(0.85, 1.1))
                img.putpixel((x, y), col + (255,))
                glow.putpixel((x, y), col + (255,))
        lights = p.get("spots")
        if lights:
            spot_col = resolve(lights, palette)
            count = max(1, int(fw * fh * p.get("spot_density", 0.08)))
            for _ in range(count):
                x = fu + rnd.randrange(fw)
                y = fv + rnd.randrange(fh)
                img.putpixel((x, y), shade(spot_col, FACE_SHADE[face] * rnd.uniform(0.9, 1.05)) + (255,))
    # details: [face, x, y, w, h, color, glow]
    for det in p.get("details", []):
        face, x, y, w, h, color = det[:6]
        is_glow = det[6] if len(det) > 6 else False
        fu, fv, fw, fh = cube.faces()[face]
        col = resolve(color, palette)
        for yy in range(h):
            for xx in range(w):
                px, py = x + xx, y + yy
                if px < 0:
                    px += fw
                if py < 0:
                    py += fh
                if 0 <= px < fw and 0 <= py < fh:
                    img.putpixel((fu + px, fv + py), col + (255,))
                    if is_glow:
                        glow.putpixel((fu + px, fv + py), col + (255,))


def pattern_color(pattern, base, accent, x, y, w, h, face, rnd):
    if pattern == "fur":
        # vertical streaks with a ragged, darker lower edge
        streak = ((x * 7 + (x * x) % 5) % 4)
        if streak == 0:
            return shade(base, 0.85)
        if streak == 3:
            return shade(base, 1.07)
        return base
    if pattern == "chitin":
        if y % 4 == 3:
            return accent
        if y % 4 == 0:
            return shade(base, 1.1)
        return base
    if pattern == "stripes":
        return accent if (y + x // 3) % 5 in (0, 1) else base
    if pattern == "bands":
        return accent if y % 3 == 0 else base
    if pattern == "veins":
        return accent if (x + 2 * y) % 7 == 0 or (2 * x - y) % 9 == 0 else base
    if pattern == "plates":
        return accent if x % 4 == 3 or y % 5 == 4 else base
    if pattern == "checker":
        return accent if (x // 2 + y // 2) % 2 == 0 else base
    if pattern == "wing":
        # monarch butterfly wing: black edges and veins, orange cells, white spots on the rim
        edge = x <= 1 or x >= w - 2 or y <= 1 or y >= h - 2
        if edge:
            if (x + y) % 3 == 0 and (x in (0, w - 1) or y in (0, h - 1)):
                return (240, 236, 220)
            return accent
        if x % 5 == 2 or (y * 3 + x) % 11 == 0:
            return accent
        return base
    return base
