"""Procedurally generates all block (and fluid / portal) textures of the mod."""
import math
import random

import numpy as np

from texlib import (clamp8, from_ascii, get, hex2rgb, jitter, mix, new, palette_map, put, save, save_mcmeta,
                    shade, value_noise)

B = "textures/block"

# ---------------------------------------------------------------- palettes
SIFTSTONE = ["#6e2a22", "#8a3628", "#a5452f", "#b9543a", "#c96745", "#d47d55"]
CARAPACE_SAND = ["#a88d68", "#b89d76", "#c7ae86", "#d4bd95", "#dfcaa4"]
GREEN_SCULK = ["#0f2624", "#173632", "#1f4640", "#2a5850", "#356a5f"]
ORANGE_SCULK = ["#2e130d", "#451d13", "#5c2819", "#73341f", "#8a4227"]
GRASS = {
    "orange": ["#8f3f35", "#a54b3f", "#b85a4a", "#c96b58", "#d67d68"],
    "light_orange": ["#d48f88", "#e09f97", "#eab0a7", "#f2c1b7", "#f8d2c8"],
    "green": ["#2d5e3c", "#376e46", "#427f50", "#4f905c", "#5da168"],
}
WILLOW_BARK = ["#5e5d58", "#706e68", "#83817a", "#96948c", "#a8a69e"]
WILLOW_STRIPPED = ["#b9b4a6", "#c6c1b3", "#d2cdbf", "#ddd8ca", "#e7e3d6"]
WILLOW_PLANKS = ["#a9a496", "#b9b4a6", "#c8c3b5", "#d5d0c2", "#e0dccf"]
WILLOW_LEAVES = ["#b7bdb8", "#c9cec9", "#d9ddd8", "#e7eae6", "#f4f6f3"]


def stone(palette, seed, cell=4, octaves=3):
    v = value_noise(16, 16, cell, seed, octaves)
    v = (v - v.min()) / (v.max() - v.min() + 1e-9)
    img = palette_map(v, palette)
    return jitter(img, 0.05, seed)


def bricks(palette, seed, mortar_factor=0.62, brick_h=4, brick_w=8):
    base = stone(palette, seed, cell=3, octaves=2)
    rnd = random.Random(seed)
    px = base.load()
    tones = {}
    for y in range(16):
        row = y // brick_h
        off = (brick_w // 2) if row % 2 else 0
        for x in range(16):
            col = ((x + off) % 16) // brick_w
            key = (row, col)
            if key not in tones:
                tones[key] = rnd.uniform(0.9, 1.08)
            r, g, b, a = px[x, y]
            f = tones[key]
            if y % brick_h == brick_h - 1 or (x + off) % brick_w == brick_w - 1:
                f = mortar_factor
            elif y % brick_h == 0:
                f *= 1.08
            px[x, y] = (clamp8(r * f), clamp8(g * f), clamp8(b * f), a)
    return base


def polished(palette, seed):
    img = stone(palette[1:], seed, cell=8, octaves=2)
    px = img.load()
    for y in range(16):
        for x in range(16):
            r, g, b, a = px[x, y]
            f = 1.0
            if x == 0 or y == 0:
                f = 1.15
            elif x == 15 or y == 15:
                f = 0.72
            elif x == 1 or y == 1:
                f = 1.05
            px[x, y] = (clamp8(r * f), clamp8(g * f), clamp8(b * f), a)
    return img


def tile_bricks(palette, seed):
    """Square tiles - used for the resonant deepslate."""
    img = stone(palette, seed, cell=4, octaves=2)
    px = img.load()
    for y in range(16):
        for x in range(16):
            r, g, b, a = px[x, y]
            f = 1
            if x % 8 == 7 or y % 8 == 7:
                f = 0.55
            elif x % 8 == 0 or y % 8 == 0:
                f = 1.12
            px[x, y] = (clamp8(r * f), clamp8(g * f), clamp8(b * f), a)
    return img


def healthy_sculk(palette, spot, seed):
    """Sculk-like cellular texture: dark cells with bright spots."""
    rnd = random.Random(seed)
    pts = [(rnd.uniform(0, 16), rnd.uniform(0, 16)) for _ in range(9)]
    img = new(16, 16)
    pal = [hex2rgb(c) for c in palette]
    bright = rnd.sample(range(len(pts)), 3)
    for y in range(16):
        for x in range(16):
            d = []
            for i, (px_, py_) in enumerate(pts):
                dx = min(abs(x - px_), 16 - abs(x - px_))
                dy = min(abs(y - py_), 16 - abs(y - py_))
                d.append((math.hypot(dx, dy), i))
            d.sort()
            edge = d[1][0] - d[0][0]
            cell = d[0][1]
            if edge < 0.9:
                c = pal[0]
            else:
                lvl = 1 + min(len(pal) - 2, int(d[0][0] / 2.2))
                lvl = len(pal) - lvl
                c = pal[max(1, min(len(pal) - 1, lvl))]
            if cell in bright and d[0][0] < 1.3:
                c = hex2rgb(spot)
            put(img, x, y, c)
    return jitter(img, 0.06, seed)


def grass_top(palette, seed):
    v = value_noise(16, 16, 3, seed, 2)
    rnd = np.random.default_rng(seed)
    v = 0.6 * v + 0.4 * rnd.random((16, 16))
    img = palette_map(v, palette)
    return img


def grass_side(dirt_img, palette, seed):
    img = dirt_img.copy()
    top = grass_top(palette, seed + 7)
    rnd = random.Random(seed)
    for x in range(16):
        depth = 3 + (1 if rnd.random() < 0.5 else 0) + (2 if rnd.random() < 0.2 else 0)
        for y in range(depth):
            c = top.getpixel((x, y))
            if y == depth - 1:
                c = tuple(shade(c[:3], 0.8)) + (255,)
            img.putpixel((x, y), c)
    return img


def plant(palette, seed, blades=13, height=(5, 14), width=16, img_h=16, lean=0.25, base_y=None):
    rnd = random.Random(seed)
    img = new(width, img_h)
    pal = [hex2rgb(c) for c in palette]
    base_y = img_h - 1 if base_y is None else base_y
    for _ in range(blades):
        x = rnd.uniform(2, width - 3)
        h = rnd.randint(*height)
        dx = rnd.uniform(-lean, lean)
        for i in range(h):
            yy = base_y - i
            t = i / max(1, h - 1)
            c = pal[min(len(pal) - 1, int(t * len(pal)))]
            put(img, int(round(x)), yy, c)
            if i < h * 0.45:
                put(img, int(round(x)) + 1, yy, shade(c, 0.85))
            x += dx + rnd.uniform(-0.15, 0.15)
    return img


def tall_plant(palette, seed):
    full = plant(palette, seed, blades=8, height=(16, 29), img_h=32, lean=0.18)
    bottom = full.crop((0, 16, 16, 32))
    top = full.crop((0, 0, 16, 16))
    return bottom, top


def log_side(palette, seed):
    v = value_noise(16, 16, 2, seed, 2)
    stretch = value_noise(16, 64, 2, seed + 1, 1)[:16, :]
    vv = np.zeros((16, 16))
    for y in range(16):
        for x in range(16):
            vv[y, x] = 0.5 * v[(y // 4) % 16, x] + 0.5 * stretch[y, x]
    img = palette_map((vv - vv.min()) / (vv.max() - vv.min() + 1e-9), palette)
    px = img.load()
    rnd = random.Random(seed)
    for x in [rnd.randint(0, 15) for _ in range(3)]:
        for y in range(16):
            if rnd.random() < 0.8:
                r, g, b, a = px[x, y]
                px[x, y] = (clamp8(r * 0.75), clamp8(g * 0.75), clamp8(b * 0.75), a)
    return img


def log_top(bark, inner, seed):
    img = new(16, 16)
    pal = [hex2rgb(c) for c in inner]
    rnd = random.Random(seed)
    for y in range(16):
        for x in range(16):
            if x in (0, 15) or y in (0, 15):
                put(img, x, y, hex2rgb(bark[rnd.randint(0, len(bark) - 1)]))
                continue
            d = max(abs(x - 7.5), abs(y - 7.5))
            ring = int(d) % 3
            c = pal[2 + (1 if ring == 0 else 0)] if ring != 1 else pal[1]
            put(img, x, y, c)
    return jitter(img, 0.04, seed)


def planks(palette, seed):
    img = new(16, 16)
    pal = [hex2rgb(c) for c in palette]
    rnd = random.Random(seed)
    grain = value_noise(16, 16, 4, seed, 2)
    for y in range(16):
        row = y // 4
        for x in range(16):
            t = 0.35 + 0.5 * grain[y, (x * 3 + row * 5) % 16]
            c = pal[min(len(pal) - 1, int(t * len(pal)))]
            if y % 4 == 3:
                c = shade(pal[0], 0.85)
            elif y % 4 == 0:
                c = shade(c, 1.05)
            put(img, x, y, c)
    for row in range(4):
        sx = (row * 7 + 3) % 16
        for y in range(row * 4, row * 4 + 3):
            put(img, sx, y, shade(pal[0], 0.9))
    return jitter(img, 0.03, seed)


def leaves(palette, seed, hole_chance=0.09):
    img = stone(palette, seed, cell=3, octaves=2)
    rnd = random.Random(seed)
    px = img.load()
    for y in range(16):
        for x in range(16):
            if rnd.random() < hole_chance:
                px[x, y] = (0, 0, 0, 0)
    # a few darker leaf outlines
    for _ in range(14):
        x, y = rnd.randint(0, 15), rnd.randint(0, 15)
        r, g, b, a = px[x, y]
        if a:
            px[x, y] = (clamp8(r * 0.8), clamp8(g * 0.8), clamp8(b * 0.8), a)
    return img


def weeping(palette, seed):
    rnd = random.Random(seed)
    img = new(16, 16)
    pal = [hex2rgb(c) for c in palette]
    for x in range(1, 15, 2):
        if rnd.random() < 0.15:
            continue
        xx = x + rnd.randint(-1, 1)
        length = rnd.randint(9, 16)
        for y in range(length):
            c = pal[max(0, len(pal) - 1 - y // 4)]
            put(img, xx, y, c)
            if rnd.random() < 0.3:
                put(img, xx + rnd.choice((-1, 1)), y, shade(c, 0.9))
    return img


def sapling(trunk, leaves_pal, seed):
    rnd = random.Random(seed)
    img = new(16, 16)
    for y in range(7, 16):
        put(img, 7 + (1 if y < 10 else 0), y, trunk[2 if y % 3 else 1])
    for y in range(1, 10):
        for x in range(3, 13):
            d = math.hypot(x - 7.5, (y - 5) * 1.3)
            if d < 4.8 and rnd.random() < 0.85:
                put(img, x, y, leaves_pal[rnd.randint(1, len(leaves_pal) - 1)])
    # weeping strands
    for x in (4, 6, 9, 11):
        for y in range(8, 8 + rnd.randint(2, 5)):
            put(img, x, y, leaves_pal[3])
    return img


def door(palette, seed, top):
    img = planks(palette, seed)
    img = img.rotate(90)
    px = img.load()
    frame = shade(hex2rgb(palette[0]), 0.85)
    for y in range(16):
        for x in range(16):
            if x in (0, 15) or (top and y == 0) or (not top and y == 15):
                put(img, x, y, frame)
    if top:
        for (x0, y0) in ((3, 3), (9, 3)):
            for y in range(y0, y0 + 5):
                for x in range(x0, x0 + 4):
                    px[x, y] = (0, 0, 0, 0)
            for x in range(x0 - 1, x0 + 5):
                put(img, x, y0 - 1, frame)
                put(img, x, y0 + 5, frame)
            for y in range(y0 - 1, y0 + 6):
                put(img, x0 - 1, y, frame)
                put(img, x0 + 4, y, frame)
    else:
        put(img, 12, 1, "#3d3b36")
        put(img, 12, 2, "#3d3b36")
    return img


def trapdoor(palette, seed):
    img = planks(palette, seed)
    px = img.load()
    frame = shade(hex2rgb(palette[0]), 0.85)
    for y in range(16):
        for x in range(16):
            if x in (0, 15) or y in (0, 15) or x in (7, 8):
                put(img, x, y, frame)
            elif (2 <= y <= 5 or 10 <= y <= 13) and (2 <= x <= 5 or 10 <= x <= 13):
                px[x, y] = (0, 0, 0, 0)
    return img


def soul_block(seed):
    img = new(16, 16)
    rnd = random.Random(seed)
    base = value_noise(16, 16, 4, seed, 2)
    for y in range(16):
        for x in range(16):
            t = base[y, x]
            c = mix("#0b1a3a", "#1d3f7a", t)
            put(img, x, y, c)
    # wisps
    for i in range(5):
        cx, cy = rnd.uniform(0, 16), rnd.uniform(0, 16)
        for s in range(14):
            a = s * 0.45 + i
            r = 0.5 + s * 0.35
            x = int(cx + math.cos(a) * r) % 16
            y = int(cy + math.sin(a) * r) % 16
            put(img, x, y, mix("#5fe3ff", "#c8f8ff", s / 14))
    # tiny soul faces
    for (fx, fy) in ((3, 4), (10, 10)):
        put(img, fx, fy, "#e8feff")
        put(img, fx + 2, fy, "#e8feff")
        put(img, fx + 1, fy + 2, "#9ef2ff")
    return img


def deepslate_tiles(seed):
    pal = ["#26262b", "#2f2f35", "#38383f", "#424249", "#4c4c54"]
    img = tile_bricks(pal, seed)
    rnd = random.Random(seed)
    # glowing echo cracks
    for _ in range(3):
        x, y = rnd.randint(1, 14), rnd.randint(1, 14)
        for _s in range(7):
            put(img, x, y, rnd.choice(["#1fb5c4", "#2fd8e6", "#77f2fb"]))
            x = (x + rnd.choice((-1, 0, 1))) % 16
            y = (y + rnd.choice((0, 1))) % 16
    return img


def animated_swirl(colors, frames, size, seed, alpha=255, speed=1.0, scale=1.0, amp=1.0):
    """Animated, tileable swirl used for ichor and the portal."""
    strip = new(size, size * frames)
    rnd = random.Random(seed)
    waves = [(rnd.uniform(0.5, 2.0), rnd.uniform(0, 6.28), rnd.choice((1, 2)), rnd.choice((-1, 1, 2)))
             for _ in range(4)]
    cols = [hex2rgb(c) for c in colors]
    for f in range(frames):
        ph = 2 * math.pi * f / frames
        for y in range(size):
            for x in range(size):
                u = 2 * math.pi * x / size
                v = 2 * math.pi * y / size
                val = 0
                for wamp, off, kx, ky in waves:
                    val += amp * wamp * math.sin(kx * u * scale + ky * v * scale + off + ph * speed * kx)
                val += 0.6 * math.sin(u + 2 * v + ph)
                t = (math.sin(val) + 1) / 2
                pos = t * (len(cols) - 1)
                i = int(pos)
                c = mix(cols[i], cols[min(i + 1, len(cols) - 1)], pos - i)
                a = alpha if isinstance(alpha, int) else alpha(t)
                strip.putpixel((x, y + f * size), (c[0], c[1], c[2], a))
    return strip


def churning(colors, frames, size, seed, alpha=235, cell=None):
    """Thick liquid: two tileable noise fields cross-faded in a loop, mapped onto a smooth colour ramp."""
    cell = cell or size // 2
    a = value_noise(size, size, cell, seed, 2)
    b = value_noise(size, size, cell, seed + 1, 2)
    c = value_noise(size, size, max(2, cell // 2), seed + 2, 1)
    cols = [hex2rgb(x) for x in colors]
    strip = new(size, size * frames)
    for f in range(frames):
        t = f / frames * 2 * math.pi
        wa, wb = (math.cos(t) + 1) / 2, (math.sin(t) + 1) / 2
        shift = int(f * size / frames)
        for y in range(size):
            for x in range(size):
                v = (a[y, x] * wa + b[(y + shift) % size, x] * (1 - wa) + c[y, (x + shift) % size] * wb * 0.6) / 1.6
                v = min(0.999, max(0.0, (v - 0.2) / 0.6))
                pos = v * (len(cols) - 1)
                i = int(pos)
                col = mix(cols[i], cols[min(i + 1, len(cols) - 1)], pos - i)
                strip.putpixel((x, y + f * size), (col[0], col[1], col[2], alpha))
    return strip


def barrier(seed):
    img = new(16, 16)
    for y in range(16):
        for x in range(16):
            edge = x in (0, 15) or y in (0, 15)
            diag = (x + y) % 6 == 0 or (x - y) % 6 == 0
            if edge:
                put(img, x, y, "#ff8fd8", 230)
            elif diag:
                put(img, x, y, "#f06bc4", 140)
            else:
                put(img, x, y, "#e05ab4", 70)
    return img


def main():
    # stone family
    save(stone(SIFTSTONE, 11), B, "siftstone.png")
    save(stone(["#5a2119"] + SIFTSTONE[:-1], 12, cell=2, octaves=2), B, "cobbled_siftstone.png")
    save(bricks(SIFTSTONE, 13), B, "siftstone_bricks.png")
    save(polished(SIFTSTONE, 14), B, "polished_siftstone.png")
    chis = polished(SIFTSTONE, 15)
    for y in range(4, 12):
        for x in range(4, 12):
            if x in (4, 11) or y in (4, 11):
                put(chis, x, y, "#6e2a22")
            elif (x + y) % 3 == 0:
                put(chis, x, y, "#e09468")
    save(chis, B, "chiseled_siftstone.png")

    # ores in siftstone
    ores = {
        "coal": ["#1c1c1c", "#2f2f2f", "#444444"],
        "iron": ["#c99a7a", "#e2b896", "#f2d2b6"],
        "copper": ["#a85a32", "#d07a48", "#5fb89a"],
        "gold": ["#c99a1e", "#f2d14b", "#fff28a"],
        "diamond": ["#1fa8a0", "#4fe6dc", "#b8fff6"],
    }
    for i, (ore, pal) in enumerate(ores.items()):
        img = stone(SIFTSTONE, 11)
        rnd = random.Random(300 + i)
        for _ in range(5):
            cx, cy = rnd.randint(2, 13), rnd.randint(2, 13)
            for dx, dy in ((0, 0), (1, 0), (0, 1), (1, 1), (-1, 0), (0, -1)):
                if rnd.random() < 0.8:
                    put(img, cx + dx, cy + dy, pal[rnd.randint(0, 2)])
            put(img, cx, cy, pal[2])
        save(img, B, f"siftstone_{ore}_ore.png")

    # carapace
    save(stone(CARAPACE_SAND, 21, cell=2, octaves=2), B, "carapace_sand.png")
    ss_side = stone(CARAPACE_SAND, 22, cell=4, octaves=2)
    px = ss_side.load()
    for y in range(16):
        for x in range(16):
            if y in (3, 9, 13):
                r, g, b, a = px[x, y]
                px[x, y] = (clamp8(r * 0.85), clamp8(g * 0.85), clamp8(b * 0.85), a)
    save(ss_side, B, "carapace_sandstone.png")
    save(polished(CARAPACE_SAND, 23), B, "carapace_sandstone_top.png")
    save(stone(CARAPACE_SAND[:-1], 24, cell=4), B, "carapace_sandstone_bottom.png")

    # healthy sculk + sculk grass
    green_dirt = healthy_sculk(GREEN_SCULK, "#43d6c2", 31)
    orange_dirt = healthy_sculk(ORANGE_SCULK, "#ffb36b", 32)
    save(green_dirt, B, "green_healthy_sculk.png")
    save(orange_dirt, B, "orange_healthy_sculk.png")
    for i, (name, pal) in enumerate(GRASS.items()):
        dirt = green_dirt if name == "green" else orange_dirt
        save(grass_top(pal, 40 + i), B, f"{name}_sculk_grass_block_top.png")
        save(grass_side(dirt, pal, 50 + i), B, f"{name}_sculk_grass_block_side.png")
        save(plant(pal, 60 + i), B, f"{name}_short_sculk_grass.png")
    bottom, top = tall_plant(GRASS["orange"], 70)
    save(bottom, B, "tall_sculk_grass_bottom.png")
    save(top, B, "tall_sculk_grass_top.png")
    bottom, top = tall_plant(GRASS["green"], 71)
    save(bottom, B, "green_tall_sculk_grass_bottom.png")
    save(top, B, "green_tall_sculk_grass_top.png")

    # white willow
    save(log_side(WILLOW_BARK, 80), B, "white_willow_log.png")
    save(log_top(WILLOW_BARK, WILLOW_STRIPPED, 81), B, "white_willow_log_top.png")
    save(log_side(WILLOW_STRIPPED, 82), B, "stripped_white_willow_log.png")
    save(log_top(WILLOW_STRIPPED, WILLOW_PLANKS, 83), B, "stripped_white_willow_log_top.png")
    save(planks(WILLOW_PLANKS, 84), B, "white_willow_planks.png")
    save(leaves(WILLOW_LEAVES, 85), B, "white_willow_leaves.png")
    save(weeping(WILLOW_LEAVES, 86), B, "weeping_white_willow.png")
    save(sapling(WILLOW_BARK, WILLOW_LEAVES, 87), B, "white_willow_sapling.png")
    save(door(WILLOW_PLANKS, 88, True), B, "white_willow_door_top.png")
    save(door(WILLOW_PLANKS, 88, False), B, "white_willow_door_bottom.png")
    save(trapdoor(WILLOW_PLANKS, 89), B, "white_willow_trapdoor.png")

    # misc
    save(soul_block(90), B, "soul_block.png")
    save(deepslate_tiles(91), B, "resonant_deepslate.png")
    save(barrier(92), B, "spectral_barrier.png")

    # ichor (multicoloured, mostly red and blue)
    ichor_cols = ["#3a0a2e", "#6a0f3a", "#a5163f", "#c8263f", "#b0307a", "#6a36b8", "#3a5bd9", "#ff8a5c"]
    save(churning(ichor_cols, 32, 16, 100, alpha=225, cell=4), B, "ichor_still.png")
    save_mcmeta({"animation": {"frametime": 4, "interpolate": True}}, B, "ichor_still.png.mcmeta")
    save(churning(ichor_cols, 32, 32, 101, alpha=225, cell=8), B, "ichor_flow.png")
    save_mcmeta({"animation": {"frametime": 2, "interpolate": True}}, B, "ichor_flow.png.mcmeta")

    # portal
    portal_cols = ["#06232b", "#0b5560", "#16a2a8", "#5ff2e8", "#c4fff8", "#2b7fc0", "#123a6e"]
    save(animated_swirl(portal_cols, 32, 16, 110, alpha=lambda t: int(150 + 90 * t), speed=1.0, scale=1.0),
         B, "sift_portal.png")
    save_mcmeta({"animation": {"frametime": 2, "interpolate": True}}, B, "sift_portal.png.mcmeta")


if __name__ == "__main__":
    main()
