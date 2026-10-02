"""Defines the geometry of every mob of the mod and paints its textures."""
import math

from PIL import Image

from geo import Model
from texlib import out_path


# ------------------------------------------------------------------------------------------------ blub
def blub():
    m = Model("blub", 64)
    body = m.bone("body", pivot=(0, 24, 0))
    body.cube((-3.5, -6, -4), (7, 5, 8), {"color": "base", "spots": "light", "glow_spots": "glow", "spot_density": 0.05,
                                           "faces": {"down": "belly"}})
    head = m.bone("head", parent="body", pivot=(0, -5, -3.5))
    head.cube((-3, -5, -4), (6, 5, 5), {"color": "base", "glow_spots": "glow", "spot_density": 0.03, "details": [
        ["north", 0, 1, 2, 2, "#141414"], ["north", 4, 1, 2, 2, "#141414"],
        ["north", 0, 1, 1, 1, "#ffffff"], ["north", 4, 1, 1, 1, "#ffffff"],
        ["north", 2, 3, 2, 1, "inner"],
    ]})
    m.bone("ear_left", parent="head", pivot=(-2, -5, -1), rotation=(-15, 0, -20)).cube(
        (-1, -7, -0.5), (2, 7, 1), {"color": "base", "faces": {"north": "inner"}})
    m.bone("ear_right", parent="head", pivot=(2, -5, -1), rotation=(-15, 0, 20)).cube(
        (-1, -7, -0.5), (2, 7, 1), {"color": "base", "faces": {"north": "inner"}})
    m.bone("tail", parent="body", pivot=(0, -4, 4)).cube((-1.5, -1.5, 0), (3, 3, 2), {"color": "light", "glow_spots": "glow"})
    for name, x, z in (("leg_front_left", -2, -2.5), ("leg_front_right", 2, -2.5), ("leg_back_left", -2.5, 2.5),
                       ("leg_back_right", 2.5, 2.5)):
        m.bone(name, pivot=(x, 23, z)).cube((-1, 0, -1.5), (2, 1, 3), {"color": "inner", "outline": False})
    m.pack()
    m.write_geo()
    variants = {
        "soul_engorged": {"base": "#3fb8d8", "light": "#7ee0f2", "belly": "#a8eefa", "inner": "#2a7fa8", "glow": "#c8fbff"},
        "carapace": {"base": "#d6b98c", "light": "#ead6b0", "belly": "#efe0c4", "inner": "#a8845a", "glow": None},
        "meadows": {"base": "#e8968a", "light": "#f6c6b8", "belly": "#f8d8cc", "inner": "#c96b6b", "glow": None},
        "ravines": {"base": "#6e4a8e", "light": "#9a6ab8", "belly": "#b08ac8", "inner": "#4a2f62", "glow": "#ff6a8a"},
    }
    for name, pal in variants.items():
        palette = dict(pal)
        if palette["glow"] is None:
            palette["glow"] = palette["light"]
            # paint without glow: temporarily use a model copy without glow spots
            for c in m.cubes():
                c.paint = dict(c.paint)
                if "glow_spots" in c.paint:
                    c.paint["_glow_backup"] = c.paint.pop("glow_spots")
                    c.paint.setdefault("spots", "light")
            m.paint("blub/" + name, palette, seed=11)
            for c in m.cubes():
                if "_glow_backup" in c.paint:
                    c.paint["glow_spots"] = c.paint.pop("_glow_backup")
        else:
            m.paint("blub/" + name, palette, seed=11)


# ------------------------------------------------------------------------------------------------ singer
def singer():
    m = Model("singer", 128)
    fur = {"color": "fur", "pattern": "fur", "accent": "fur_dark"}
    m.bone("leg_left", pivot=(-3.5, 8, 0)).cube((-2.5, 0, -2.5), (5, 16, 5), dict(fur, faces={"down": "hoof"}))
    m.bone("leg_right", pivot=(3.5, 8, 0)).cube((-2.5, 0, -2.5), (5, 16, 5), dict(fur, faces={"down": "hoof"}))
    body = m.bone("body", pivot=(0, 8, 0))
    body.cube((-6, -16, -4), (12, 16, 8), dict(fur, spots="fur_light", spot_density=0.05))
    body.cube((-6.5, -4, -4.5), (13, 6, 9), dict(fur, color="fur_dark", accent="fur"))
    neck = m.bone("neck", parent="body", pivot=(0, -15, -2), rotation=(10, 0, 0))
    neck.cube((-1.5, -12, -1.5), (3, 12, 3), dict(fur, color="fur_light"))
    head = m.bone("head", parent="neck", pivot=(0, -12, 0))
    head.cube((-2.5, -4, -3.5), (5, 4, 5), {"color": "fur_light", "faces": {"north": "face"}, "details": [
        ["north", 1, 1, 1, 1, "eye", True], ["north", 3, 1, 1, 1, "eye", True], ["north", 2, 3, 1, 1, "#2a3328"],
    ]})
    al = m.bone("antler_left", parent="head", pivot=(-2, -4, 0), rotation=(0, 0, -20))
    al.cube((-0.5, -7, -0.5), (1, 7, 1), {"color": "antler", "outline": False})
    al.cube((-3, -4, -0.5), (3, 1, 1), {"color": "antler", "outline": False})
    al.cube((-2, -7, -0.5), (2, 1, 1), {"color": "antler", "outline": False})
    ar = m.bone("antler_right", parent="head", pivot=(2, -4, 0), rotation=(0, 0, 20))
    ar.cube((-0.5, -7, -0.5), (1, 7, 1), {"color": "antler", "outline": False})
    ar.cube((0, -4, -0.5), (3, 1, 1), {"color": "antler", "outline": False})
    ar.cube((0, -7, -0.5), (2, 1, 1), {"color": "antler", "outline": False})
    m.bone("arm_left", parent="body", pivot=(-7, -14, 0)).cube((-2, -1, -2), (4, 20, 4), dict(fur, faces={"down": "hoof"}))
    m.bone("arm_right", parent="body", pivot=(7, -14, 0)).cube((-2, -1, -2), (4, 20, 4), dict(fur, faces={"down": "hoof"}))
    m.pack()
    m.write_geo()
    m.paint("singer", {"fur": "#a9c79a", "fur_dark": "#86a578", "fur_light": "#c4dcb4", "face": "#55684f",
                       "eye": "#e6fff0", "antler": "#ece6d0", "hoof": "#6d7f63"}, seed=21)


# ------------------------------------------------------------------------------------------------ echo golem
def echo_golem():
    m = Model("echo_golem", 64)
    plate = {"color": "body", "pattern": "plates", "accent": "body_dark"}
    m.bone("leg_left", pivot=(-2.5, 16, 0)).cube((-2, 0, -2), (4, 8, 4), plate)
    m.bone("leg_right", pivot=(2.5, 16, 0)).cube((-2, 0, -2), (4, 8, 4), plate)
    body = m.bone("body", pivot=(0, 16, 0))
    body.cube((-5, -12, -3.5), (10, 12, 7), dict(plate, glow_spots="glow_dim", spot_density=0.015))
    m.bone("core", parent="body", pivot=(0, -8, -3.5)).cube((-2, -2, -1), (4, 4, 1), {"color": "core", "glow": True, "outline": False})
    head = m.bone("head", parent="body", pivot=(0, -12, -0.5))
    head.cube((-3.5, -7, -3.5), (7, 7, 7), dict(plate, details=[
        ["north", 1, 3, 2, 1, "eye", True], ["north", 4, 3, 2, 1, "eye", True]]))
    crystal = m.bone("crystal", parent="head", pivot=(0, -7, 0))
    crystal.cube((-1, -4, -1), (2, 4, 2), {"color": "core", "glow": True, "outline": False})
    crystal.cube((1, -2.5, -0.5), (1, 2, 1), {"color": "core", "glow": True, "outline": False})
    m.bone("arm_left", parent="body", pivot=(-6.5, -10, 0)).cube((-1.5, -1, -2), (3, 13, 4), plate)
    m.bone("arm_right", parent="body", pivot=(6.5, -10, 0)).cube((-1.5, -1, -2), (3, 13, 4), plate)
    m.pack()
    m.write_geo()
    m.paint("echo_golem", {"body": "#24474e", "body_dark": "#16303a", "core": "#49e6f2", "eye": "#9ff9ff",
                           "glow_dim": "#2fb8c4"}, seed=31)


# ------------------------------------------------------------------------------------------------ seedling
def seedling():
    m = Model("seedling", 64)
    body = m.bone("body", pivot=(0, 22, 0))
    body.cube((-3, -6, -3), (6, 6, 6), {"color": "skin", "spots": "spot", "details": [
        ["north", 1, 1, 1, 1, "eye", True], ["north", 4, 1, 1, 1, "eye", True]]})
    m.bone("upper_jaw", parent="body", pivot=(0, -3, -3)).cube((-2.5, -2, -4), (5, 2, 4), {
        "color": "skin", "faces": {"down": "mouth"}, "details": [["down", 0, 0, 5, 1, "teeth"], ["down", 0, 3, 5, 1, "teeth"]]})
    m.bone("lower_jaw", parent="body", pivot=(0, -1, -3)).cube((-2.5, 0, -4), (5, 2, 4), {
        "color": "skin", "faces": {"up": "mouth"}, "details": [["up", 0, 0, 5, 1, "teeth"]]})
    m.bone("stem", parent="body", pivot=(0, -6, 0)).cube((-0.5, -3, -0.5), (1, 3, 1), {"color": "leaf", "outline": False})
    m.bone("leaf_left", parent="body", pivot=(-0.5, -8, 0), rotation=(0, 0, 25)).cube((-4, -1, -1), (4, 1, 2), {"color": "leaf"})
    m.bone("leaf_right", parent="body", pivot=(0.5, -8, 0), rotation=(0, 0, -25)).cube((0, -1, -1), (4, 1, 2), {"color": "leaf"})
    for name, x, z in (("leg_front_left", -2, -2), ("leg_front_right", 2, -2), ("leg_back_left", -2, 2), ("leg_back_right", 2, 2)):
        m.bone(name, pivot=(x, 22, z)).cube((-1, 0, -1), (2, 2, 2), {"color": "skin_dark", "outline": False})
    m.pack()
    m.write_geo()
    m.paint("seedling", {"skin": "#5e9e4a", "skin_dark": "#3f6e33", "spot": "#8cc66a", "eye": "#f2d14b",
                         "mouth": "#5a1e2a", "teeth": "#f0ecd8", "leaf": "#7fc25a"}, seed=41)


# ------------------------------------------------------------------------------------------------ sentinel
def sentinel():
    m = Model("sentinel", 64)
    skin = {"color": "skin", "pattern": "bands", "accent": "skin_dark"}
    m.bone("leg_left", pivot=(-2.5, 14, 1)).cube((-1.5, 0, -1.5), (3, 10, 3), skin)
    m.bone("leg_right", pivot=(2.5, 14, 1)).cube((-1.5, 0, -1.5), (3, 10, 3), skin)
    body = m.bone("body", pivot=(0, 14, 0))
    body.cube((-4, -10, -3), (8, 10, 6), dict(skin, faces={"north": "belly"}))
    head = m.bone("head", parent="body", pivot=(0, -10, -1))
    head.cube((-3, -6, -4), (6, 6, 6), {"color": "skin", "details": [
        ["north", 1, 2, 1, 1, "eye", True], ["north", 4, 2, 1, 1, "eye", True]]})
    m.bone("chin", parent="head", pivot=(0, 0, -3)).cube((-2, 0, -1), (4, 3, 2), {
        "color": "chin", "details": [["down", 0, 0, 4, 2, "dart"], ["north", 0, 2, 4, 1, "dart"]]})
    crest = m.bone("crest", parent="head", pivot=(0, -6, 0))
    crest.cube((-0.5, -7, -3), (1, 7, 8), {"color": "crest", "pattern": "stripes", "accent": "crest_dark"})
    crest.cube((-0.5, -3, 5), (1, 3, 3), {"color": "crest", "pattern": "stripes", "accent": "crest_dark"})
    m.bone("arm_left", parent="body", pivot=(-4.5, -8, 0)).cube((-1, 0, -1), (2, 8, 2), skin)
    m.bone("arm_right", parent="body", pivot=(4.5, -8, 0)).cube((-1, 0, -1), (2, 8, 2), skin)
    m.pack()
    m.write_geo()
    m.paint("sentinel", {"skin": "#3f7a6a", "skin_dark": "#2c5a4e", "belly": "#7fb09a", "eye": "#ffe14a",
                         "chin": "#2f5a50", "dart": "#f2efe0", "crest": "#e07b39", "crest_dark": "#9c4a1e"}, seed=51)


# ------------------------------------------------------------------------------------------------ pollinator
def pollinator():
    m = Model("pollinator", 64)
    body = m.bone("body", pivot=(0, 24, 0))
    body.cube((-5, -9, -5), (10, 7, 11), {"color": "skin", "spots": "pollen", "spot_density": 0.1, "faces": {"down": "belly"}})
    head = m.bone("head", parent="body", pivot=(0, -8, -5))
    head.cube((-5, -5, -6), (10, 5, 6), {"color": "skin", "faces": {"down": "belly"}, "details": [
        ["north", 1, 3, 8, 1, "mouth"]]})
    m.bone("eye_left", parent="head", pivot=(-3, -5, -3)).cube((-1.5, -2, -1.5), (3, 2, 3), {
        "color": "skin", "details": [["north", 1, 0, 1, 1, "eye", True], ["up", 1, 1, 1, 1, "eye", True]]})
    m.bone("eye_right", parent="head", pivot=(3, -5, -3)).cube((-1.5, -2, -1.5), (3, 2, 3), {
        "color": "skin", "details": [["north", 1, 0, 1, 1, "eye", True], ["up", 1, 1, 1, 1, "eye", True]]})
    m.bone("throat", parent="head", pivot=(0, 0, -3)).cube((-3, 0, -2.5), (6, 3, 5), {"color": "pollen", "pattern": "veins", "accent": "pollen_dark"})
    m.bone("tongue", parent="head", pivot=(0, -1, -6)).cube((-1, -0.5, -8), (2, 1, 8), {"color": "tongue", "outline": False})
    m.bone("leg_back_left", pivot=(-5, 20, 3)).cube((-2, -1, -3), (3, 5, 6), {"color": "skin", "spots": "pollen"})
    m.bone("leg_back_right", pivot=(5, 20, 3)).cube((-1, -1, -3), (3, 5, 6), {"color": "skin", "spots": "pollen"})
    m.bone("leg_front_left", pivot=(-3.5, 21, -4)).cube((-1, 0, -1), (2, 3, 2), {"color": "skin"})
    m.bone("leg_front_right", pivot=(3.5, 21, -4)).cube((-1, 0, -1), (2, 3, 2), {"color": "skin"})
    m.pack()
    m.write_geo()
    m.paint("pollinator", {"skin": "#6e8f3a", "belly": "#c9d98a", "pollen": "#f0d040", "pollen_dark": "#c49a1e",
                           "eye": "#ff9a2a", "mouth": "#3a2a1a", "tongue": "#e86a8a"}, seed=61)


# ------------------------------------------------------------------------------------------------ nester
def nester():
    m = Model("nester", 64)
    hide = {"color": "hide", "pattern": "stripes", "accent": "stripe"}
    body = m.bone("body", pivot=(0, 9, 0))
    body.cube((-3.5, -4, -6), (7, 6, 12), dict(hide, faces={"down": "belly"}))
    neck = m.bone("neck", parent="body", pivot=(0, -2, -6), rotation=(30, 0, 0))
    neck.cube((-1.5, -6, -2), (3, 7, 3), hide)
    head = m.bone("head", parent="neck", pivot=(0, -6, -0.5), rotation=(-30, 0, 0))
    head.cube((-2.5, -3, -6), (5, 4, 6), dict(hide, details=[
        ["east", 1, 1, 1, 1, "eye", True], ["west", 4, 1, 1, 1, "eye", True], ["north", 1, 1, 1, 1, "eye", True],
        ["north", 3, 1, 1, 1, "eye", True]]))
    m.bone("jaw", parent="head", pivot=(0, 1, -1)).cube((-2, 0, -5), (4, 1, 5), {"color": "belly", "details": [["up", 0, 0, 4, 1, "#f0ecd8"]]})
    m.bone("tail", parent="body", pivot=(0, -3, 6), rotation=(-20, 0, 0)).cube((-1, -1, 0), (2, 2, 7), hide)
    for name, x, z in (("leg_front_left", -3, -4.5), ("leg_front_right", 3, -4.5), ("leg_back_left", -3, 4.5), ("leg_back_right", 3, 4.5)):
        m.bone(name, pivot=(x, 10, z)).cube((-1, 0, -1), (2, 14, 2), {"color": "hide", "accent": "stripe", "pattern": "bands"})
    m.pack()
    m.write_geo()
    m.paint("nester", {"hide": "#b5562e", "stripe": "#6e2f1a", "belly": "#f1ddb8", "eye": "#ffd23a"}, seed=71)


# ------------------------------------------------------------------------------------------------ sprout / harmonizer
def sprout():
    m = Model("sprout", 64)
    bulb = m.bone("bulb", pivot=(0, 12, 0))
    bulb.cube((-4, -8, -4), (8, 8, 8), {"color": "skin", "pattern": "veins", "accent": "vein", "details": [
        ["north", 2, 2, 4, 3, "eye_white"], ["north", 3, 3, 2, 2, "eye", True]]})
    crown = m.bone("crown", parent="bulb", pivot=(0, -8, 0))
    crown.cube((-3, -3, -0.5), (6, 3, 1), {"color": "leaf"})
    crown.cube((-0.5, -3, -3), (1, 3, 6), {"color": "leaf"})
    for i in range(6):
        a = math.radians(i * 60 + 30)
        x = round(math.cos(a) * 2.5, 2)
        z = round(math.sin(a) * 2.5, 2)
        m.bone(f"tentacle_{i}", parent="bulb", pivot=(x, 0, z)).cube((-1, 0, -1), (2, 9, 2), {
            "color": "tentacle", "details": [["north", 0, 6, 2, 3, "tip", True], ["south", 0, 6, 2, 3, "tip", True],
                                             ["east", 0, 6, 2, 3, "tip", True], ["west", 0, 6, 2, 3, "tip", True],
                                             ["down", 0, 0, 2, 2, "tip", True]]})
    m.pack()
    m.write_geo()
    m.paint("sprout", {"skin": "#5fa36a", "vein": "#3f7a4a", "leaf": "#8fd06a", "eye_white": "#f0f6e0",
                       "eye": "#2a1a3a", "tentacle": "#4e8c6a", "tip": "#3ff0d8"}, seed=81)
    m.paint("harmonizer", {"skin": "#2f6b4a", "vein": "#f2c94c", "leaf": "#f2c94c", "eye_white": "#ffe9a8",
                           "eye": "#b8392a", "tentacle": "#245a3e", "tip": "#ffd84a"}, seed=82)


# ------------------------------------------------------------------------------------------------ dartback
def dartback():
    m = Model("dartback", 128)
    shell = {"color": "shell", "pattern": "chitin", "accent": "shell_dark"}
    body = m.bone("body", pivot=(0, 10, 0))
    body.cube((-7, -6, -12), (14, 9, 24), dict(shell, faces={"down": "belly"}))
    spines = m.bone("spines", parent="body", pivot=(0, -6, 0))
    for x in (-4, 0, 4):
        for z in (-9, -4, 1, 6):
            h = 6 if x == 0 else 4
            spines.cube((x - 0.5, -h, z - 0.5), (1, h, 1), {"color": "spine", "outline": False, "details": [
                ["north", 0, 0, 1, 1, "tip", True], ["south", 0, 0, 1, 1, "tip", True], ["up", 0, 0, 1, 1, "tip", True],
                ["east", 0, 0, 1, 1, "tip", True], ["west", 0, 0, 1, 1, "tip", True]]})
    head = m.bone("head", parent="body", pivot=(0, -1, -12))
    head.cube((-4, -4, -7), (8, 7, 7), dict(shell, details=[
        ["north", 1, 2, 2, 1, "eye", True], ["north", 5, 2, 2, 1, "eye", True], ["north", 2, 5, 4, 1, "#1a0a10"]]))
    m.bone("mandible_left", parent="head", pivot=(-3, 2, -7), rotation=(0, 15, 0)).cube((-1, 0, -3), (2, 2, 4), {"color": "spine"})
    m.bone("mandible_right", parent="head", pivot=(3, 2, -7), rotation=(0, -15, 0)).cube((-1, 0, -3), (2, 2, 4), {"color": "spine"})
    m.bone("tail", parent="body", pivot=(0, -3, 12), rotation=(25, 0, 0)).cube((-2.5, -2.5, 0), (5, 5, 9), shell)
    for i, z in enumerate((-8, 0, 8)):
        m.bone(f"leg_left_{i}", pivot=(-7, 11, z), rotation=(0, 0, 35)).cube((-1, -1, -1), (2, 14, 2), {"color": "shell_dark", "pattern": "bands", "accent": "shell"})
        m.bone(f"leg_right_{i}", pivot=(7, 11, z), rotation=(0, 0, -35)).cube((-1, -1, -1), (2, 14, 2), {"color": "shell_dark", "pattern": "bands", "accent": "shell"})
    m.pack()
    m.write_geo()
    m.paint("dartback", {"shell": "#7a3242", "shell_dark": "#4e1c2a", "belly": "#c48a7a", "spine": "#e8d8c0",
                         "tip": "#5fd3ff", "eye": "#5fd3ff"}, seed=91)


# ------------------------------------------------------------------------------------------------ sculkers
def sculker():
    m = Model("sculker", 64)
    skin = {"color": "skin", "glow_spots": "vein", "spot_density": 0.07}
    m.bone("leg_left", pivot=(-2.5, 12, 1)).cube((-1.5, 0, -1.5), (3, 12, 3), skin)
    m.bone("leg_right", pivot=(2.5, 12, 1)).cube((-1.5, 0, -1.5), (3, 12, 3), skin)
    body = m.bone("body", pivot=(0, 12, 0), rotation=(15, 0, 0))
    body.cube((-4.5, -12, -3), (9, 12, 6), dict(skin, details=[["north", 3, 3, 3, 4, "vein", True]]))
    head = m.bone("head", parent="body", pivot=(0, -12, -1), rotation=(-15, 0, 0))
    head.cube((-3.5, -6, -4), (7, 6, 7), dict(skin, details=[
        ["north", 1, 2, 2, 1, "eye", True], ["north", 4, 2, 2, 1, "eye", True], ["north", 2, 4, 3, 1, "#050a0c"]]))
    m.bone("horn_left", parent="head", pivot=(-3, -6, 0), rotation=(0, 0, -25)).cube((-1, -5, -0.5), (2, 5, 1), {"color": "claw"})
    m.bone("horn_right", parent="head", pivot=(3, -6, 0), rotation=(0, 0, 25)).cube((-1, -5, -0.5), (2, 5, 1), {"color": "claw"})
    for side, x in (("left", -5.5), ("right", 5.5)):
        m.bone(f"arm_{side}", parent="body", pivot=(x, -10, 0)).cube((-1.5, -1, -1.5), (3, 12, 3), skin)
        claw = m.bone(f"claw_{side}", parent=f"arm_{side}", pivot=(0, 11, 0))
        for bx in (-1.5, -0.5, 0.5):
            claw.cube((bx, 0, -1.5), (1, 7, 1), {"color": "claw", "outline": False, "details": [["north", 0, 5, 1, 2, "vein", True]]})
    m.pack()
    m.write_geo()
    kinds = {
        "hunter": ("#d9772e", "#ffb35a"),
        "scavenger": ("#e6c24a", "#fff08a"),
        "stalker": ("#3e7cc9", "#7fd0ff"),
        "trapper": ("#e07ab8", "#ffb0e0"),
    }
    for i, (kind, (claw, vein)) in enumerate(kinds.items()):
        m.paint("sculker/" + kind, {"skin": "#10242b", "vein": vein, "eye": vein, "claw": claw}, seed=101 + i)


# ------------------------------------------------------------------------------------------------ monarch
def monarch():
    m = Model("monarch", 128)
    skin = {"color": "skin", "glow_spots": "vein", "spot_density": 0.05}
    m.bone("leg_left", pivot=(-3, 2, 0)).cube((-2, 0, -2), (4, 22, 4), skin)
    m.bone("leg_right", pivot=(3, 2, 0)).cube((-2, 0, -2), (4, 22, 4), skin)
    body = m.bone("body", pivot=(0, 2, 0))
    body.cube((-6, -18, -4), (12, 18, 8), dict(skin, details=[["north", 4, 4, 4, 6, "vein", True]]))
    head = m.bone("head", parent="body", pivot=(0, -18, -1))
    head.cube((-4, -8, -4.5), (8, 8, 8), dict(skin, details=[
        ["north", 1, 3, 2, 2, "eye", True], ["north", 5, 3, 2, 2, "eye", True], ["north", 2, 6, 4, 1, "#050505"]]))
    crown = m.bone("crown", parent="head", pivot=(0, -8, 0))
    for x, h in ((-3.5, 4), (-0.5, 5), (2.5, 4)):
        crown.cube((x, -h, -0.5), (1, h, 1), {"color": "crown", "outline": False, "glow": True})
    m.bone("wing_left", parent="body", pivot=(-3, -15, 4), rotation=(0, -30, 0)).cube(
        (-14, -2, 0), (14, 22, 1), {"color": "wing", "pattern": "wing", "accent": "wing_edge", "gradient": False, "outline": False})
    m.bone("wing_right", parent="body", pivot=(3, -15, 4), rotation=(0, 30, 0)).cube(
        (0, -2, 0), (14, 22, 1), {"color": "wing", "pattern": "wing", "accent": "wing_edge", "gradient": False, "outline": False})
    for side, x in (("left", -7.5), ("right", 7.5)):
        m.bone(f"arm_{side}", parent="body", pivot=(x, -15, 0)).cube((-2, -1, -2), (4, 18, 4), skin)
        claw = m.bone(f"claw_{side}", parent=f"arm_{side}", pivot=(0, 17, 0))
        for bx in (-2, -0.5, 1):
            claw.cube((bx, 0, -1.5), (1, 12, 1), {"color": "claw", "outline": False})
    m.pack()
    m.write_geo()
    pal = {"skin": "#1a1a2e", "vein": "#ff8a1a", "eye": "#ffb03a", "crown": "#f2c14e", "wing": "#e8731a",
           "wing_edge": "#1a1010", "claw": "#f0e6d2"}
    img = m.paint("monarch", pal, seed=111)
    # the Monarch Echo: a spectral, translucent clone
    echo = img.copy()
    px = echo.load()
    for y in range(echo.height):
        for x in range(echo.width):
            r, g, b, a = px[x, y]
            if a:
                lum = (r * 0.3 + g * 0.59 + b * 0.11) / 255
                px[x, y] = (int(60 + 120 * lum), int(170 + 80 * lum), 255, 150)
    echo.save(out_path("textures", "entity", "monarch_echo.png"))


def main():
    blub()
    singer()
    echo_golem()
    seedling()
    sentinel()
    pollinator()
    nester()
    sprout()
    dartback()
    sculker()
    monarch()


if __name__ == "__main__":
    main()
