"""Item icons (16x16 pixel art) and armor layer textures.

Icons are drawn as ASCII art with fill colours only; outlines and light/shadow are added automatically.
"""
from PIL import Image

from texlib import clamp8, hex2rgb, new, out_path, save

I = "textures/item"


def shade(c, f):
    return tuple(clamp8(x * f) for x in c)


def icon(rows, palette, outline=True, auto_shade=True, glow=()):
    h = len(rows)
    w = max(len(r) for r in rows)
    assert w <= 16 and h <= 16, rows
    grid = [[None] * 16 for _ in range(16)]
    keys = [[None] * 16 for _ in range(16)]
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch not in ". ":
                grid[y][x] = hex2rgb(palette[ch])
                keys[y][x] = ch
    img = new(16, 16)
    for y in range(16):
        for x in range(16):
            c = grid[y][x]
            if c is None:
                continue
            if auto_shade and keys[y][x] not in glow:
                up_left_empty = (y == 0 or grid[y - 1][x] is None) or (x == 0 or grid[y][x - 1] is None)
                down_right_empty = (y == 15 or grid[y + 1][x] is None) or (x == 15 or grid[y][x + 1] is None)
                if up_left_empty and not down_right_empty:
                    c = shade(c, 1.18)
                elif down_right_empty and not up_left_empty:
                    c = shade(c, 0.78)
            img.putpixel((x, y), c + (255,))
    if outline:
        out = img.copy()
        for y in range(16):
            for x in range(16):
                if grid[y][x] is not None:
                    continue
                neigh = [grid[yy][xx] for yy, xx in ((y - 1, x), (y + 1, x), (y, x - 1), (y, x + 1))
                         if 0 <= yy < 16 and 0 <= xx < 16 and grid[yy][xx] is not None]
                if neigh:
                    dark = min(neigh, key=lambda c: sum(c))
                    out.putpixel((x, y), shade(dark, 0.35) + (255,))
        img = out
    return img


ICONS = {}


def add(name, rows, palette, **kw):
    ICONS[name] = (rows, palette, kw)


# ---------------------------------------------------------------------------------------------- materials
add("sifter_chitin", [
    "................",
    "................",
    "......AAAA......",
    "....AAABBAAA....",
    "...AABBBBBBAA...",
    "...ABBCCCCBBA...",
    "..AABCCCCCCBAA..",
    "..ABBCCGGCCBBA..",
    "..ABCCGGGGCCBA..",
    "..AABCCGGCCBAA..",
    "...ABBCCCCBBA...",
    "...AABBBBBBAA...",
    "....AAABBAAA....",
    "......AAAA......",
    "................",
    "................"], {"A": "#8e3b22", "B": "#c96a3a", "C": "#e8954e", "G": "#6aa84a"})
add("sculker_claw", [
    "................",
    "..........CC....",
    ".........CCD....",
    "........CCD.....",
    ".......CCD......",
    "......CCDD......",
    ".....CCDD.......",
    "....CCDD........",
    "...CCDD.........",
    "...CDD..........",
    "..SSS...........",
    ".SSSS...........",
    ".SSS............",
    "..S.............",
    "................",
    "................"], {"C": "#e8f2f4", "D": "#9fc6d0", "S": "#16343c"})
add("soul_fragment", [
    "................",
    "................",
    ".......L........",
    "......LLL.......",
    ".....LBBBL......",
    ".....LBWBL......",
    "....LBBWBBL.....",
    "....LBWWBBL.....",
    "....LBBWBBL.....",
    ".....LBBBL......",
    ".....LBBBL......",
    "......LBL.......",
    ".......L........",
    "................",
    "................",
    "................"], {"L": "#2a8fc4", "B": "#5fd3ff", "W": "#d8f8ff"}, glow="BW")
add("enraged_heart", [
    "................",
    "................",
    "...RRR...RRR....",
    "..RRRRR.RRRRR...",
    ".RRPRRRRRRRRRR..",
    ".RPPRRRRRRRRRR..",
    ".RRRRRRRRRRRRR..",
    ".RRRRRRRRRRRRR..",
    "..RRRRRRRRRRR...",
    "...RRRRRRRRR....",
    "....RRRRRRR.....",
    ".....RRRRR......",
    "......RRR.......",
    ".......R........",
    "................",
    "................"], {"R": "#c81e2a", "P": "#ff8a8a"})
add("monarch_feather", [
    "................",
    "...........BB...",
    "..........BOOB..",
    ".........BOOOB..",
    "........BOOWOB..",
    ".......BOOOOB...",
    "......BOOWOB....",
    ".....BOOOOB.....",
    "....BOOWOB......",
    "...BOOOOB.......",
    "...BOOOB........",
    "..BBOBB.........",
    "..QB............",
    ".Q..............",
    "Q...............",
    "................"], {"B": "#1a1010", "O": "#e8731a", "W": "#f6f0e0", "Q": "#3a2a20"})
add("sift_dart", [
    "................",
    "................",
    "................",
    "............TT..",
    "...........TTT..",
    "..........SSTT..",
    ".........SS.....",
    "........SS......",
    ".......SS.......",
    "......SS........",
    ".....SS.........",
    "...FFS..........",
    "...FF...........",
    "..F.............",
    "................",
    "................"], {"T": "#f2efe0", "S": "#3f7a6a", "F": "#e07b39"})
add("goo_glob", [
    "................",
    "................",
    "................",
    "......YYYY......",
    "....YYYYYYYY....",
    "...YYWYYYYYYY...",
    "...YWWYYYYYYY...",
    "..YYYYYYYYYYYY..",
    "..YYYYYYYYYYYY..",
    "..YYYYYYYYYYGY..",
    "...YYYYYYYYGG...",
    "...YYYYYYYGGY...",
    "....YYYYYYYY....",
    "......YYYY......",
    "................",
    "................"], {"Y": "#f0d040", "W": "#fff6b0", "G": "#c49a1e"})
add("soul_wisp", [
    "................",
    "................",
    ".......B........",
    "......BB........",
    ".....BBBB.......",
    "....BBWWBB......",
    "....BWWWWB......",
    "...BBWWWWBB.....",
    "...BBWWWWBB.....",
    "....BBWWBB......",
    ".....BBBB.......",
    "......BB........",
    "................",
    "................",
    "................",
    "................"], {"B": "#3fb8e0", "W": "#e0fbff"}, glow="BW")

# ---------------------------------------------------------------------------------------------- misc items
add("ichor_bucket", [
    "................",
    "................",
    "................",
    "...GGGGGGGGGG...",
    "..GIRRBRRIRRGG..",
    "..GRRIRRBRRIRG..",
    "..GGRRRRRRRRGG..",
    "...GGGGGGGGGG...",
    "...GLGGGGGGLG...",
    "...GLGGGGGGLG...",
    "....GGGGGGGG....",
    "....GLGGGGLG....",
    ".....GGGGGG.....",
    "................",
    "................",
    "................"], {"G": "#a8acb0", "L": "#d8dcdf", "R": "#c8204a", "B": "#3a5bd9", "I": "#ff7a3c"})
add("singers_horn", [
    "................",
    "................",
    "...........PP...",
    "..........PPPP..",
    ".........PPEEP..",
    "........PPPEEP..",
    ".......PPPPPP...",
    "......PPPPP.....",
    ".....PPPPP......",
    "....GGPPP.......",
    "...GGGPP........",
    "..GGGG..........",
    "..GGE...........",
    "...E............",
    "................",
    "................"], {"P": "#e6dfc8", "G": "#a9c79a", "E": "#49e6f2"}, glow="E")
add("monarch_lure", [
    "................",
    "................",
    "....Y..Y..Y.....",
    "....YY.YY.YY....",
    "....YYYYYYYY....",
    "....KKKKKKKK....",
    "...KKKKKKKKKK...",
    "...KOOKKKKOOK...",
    "...KOOKKKKOOK...",
    "...KKKKKKKKKK...",
    "....KKKSSKKK....",
    "....KSKSSKSK....",
    ".....KKKKKK.....",
    "................",
    "................",
    "................"], {"Y": "#f2c14e", "K": "#1a1a2e", "O": "#ff8a1a", "S": "#e8e2cc"}, glow="O")
add("white_willow_door", [
    "....WWWWWWWW....",
    "....WAAWWAAW....",
    "....WAAWWAAW....",
    "....WAAWWAAW....",
    "....WWWWWWWW....",
    "....WPPPPPPW....",
    "....WPPPPPPW....",
    "....WWWWWWWW....",
    "....WPPPPPPW....",
    "....WPPPPPDW....",
    "....WPPPPPDW....",
    "....WWWWWWWW....",
    "....WPPPPPPW....",
    "....WPPPPPPW....",
    "....WPPPPPPW....",
    "....WWWWWWWW...."], {"W": "#a9a496", "P": "#d5d0c2", "A": "#8fb8c8", "D": "#3d3b36"}, outline=False)

# ---------------------------------------------------------------------------------------------- weapons
add("battlestaff", [
    "................",
    "............CC..",
    "...........CCCC.",
    "..........CCCC..",
    "..........WCC...",
    ".........WW.....",
    "........WW......",
    ".......WW.......",
    "......WW........",
    ".....WW.........",
    "....WW..........",
    "..CCW...........",
    ".CCCC...........",
    "CCCC............",
    ".CC.............",
    "................"], {"C": "#e07b39", "W": "#c8c3b5"})
add("riftslasher", [
    "..............BB",
    ".............BRB",
    "............BRB.",
    "...........BRB..",
    "..........BRB...",
    ".........BRB....",
    "........BRB.....",
    ".......BRB......",
    "......BRB.......",
    "..G..BRB........",
    "..GGBRB.........",
    "...GGB..........",
    "...HGG..........",
    "..HH.G..........",
    ".HH.............",
    ".H.............."], {"B": "#c8b8e8", "R": "#b04ae0", "G": "#e8954e", "H": "#5a2a1e"}, glow="R")
add("spectral_spear", [
    "..............SS",
    ".............SWS",
    "............SWWS",
    "............SWS.",
    "...........DSS..",
    "..........DD....",
    ".........DD.....",
    "........DD......",
    ".......DD.......",
    "......DD........",
    ".....DD.........",
    "....DD..........",
    "...DD...........",
    "..DD............",
    ".DD.............",
    "................"], {"S": "#49e6f2", "W": "#e0fdff", "D": "#5a2f6a"}, glow="SW")
add("sculkers_bane", [
    "..............KK",
    ".............KTK",
    "............KTK.",
    "...........KTK..",
    "..........KTK...",
    ".........KTK....",
    "........KTK.....",
    ".......KTK......",
    "..G...KTK.......",
    "..GG.KTK........",
    "...GGTK.........",
    "....GG..........",
    "...HHGG.........",
    "..HH..G.........",
    ".HH.............",
    ".H.............."], {"K": "#16343c", "T": "#2fd8e6", "G": "#e8dcc0", "H": "#2a2018"}, glow="T")
add("sculker_claws", [
    "................",
    ".C.....C.....C..",
    ".CC....CC....CC.",
    "..CC....C....CC.",
    "..CC....CC...CC.",
    "...CC...CC..CC..",
    "...CC...CC..CC..",
    "....CC..CC.CC...",
    "....CC..CC.CC...",
    ".....SSSSSSSS...",
    ".....SSSSSSSS...",
    "......SVSSVS....",
    ".......SSSS.....",
    "................",
    "................",
    "................"], {"C": "#cfe6ee", "S": "#16343c", "V": "#3e7cc9"}, glow="V")
add("cacophonous_cleaver", [
    "................",
    "...BBBBBBBBB....",
    "..BBBBBBBBBBB...",
    "..BBBBNBBBBBB...",
    "..BBBNNBBBBBB...",
    "..BBBNNBNBBBB...",
    "..BBBBBBNNBBB...",
    "..BBBBBBNNBBB...",
    "...BBBBBBBBBB...",
    "..........GG....",
    ".........HG.....",
    "........HH......",
    ".......HH.......",
    "......HH........",
    ".....HH.........",
    "................"], {"B": "#9ea4ab", "N": "#2fd8e6", "G": "#c49a1e", "H": "#4a2f1e"}, glow="N")
add("soul_reaper", [
    "................",
    "...SSSSSSS......",
    "..SSWWWWWSSS....",
    ".SSW.....WSSH...",
    ".SW........HH...",
    "..S........HH...",
    "..........HH....",
    "..........HH....",
    ".........HH.....",
    ".........HH.....",
    "........HH......",
    "........HH......",
    ".......HH.......",
    ".......HH.......",
    "................",
    "................"], {"S": "#3fb8e0", "W": "#d8f8ff", "H": "#3a2a20"}, glow="SW")

# ---------------------------------------------------------------------------------------------- artifacts
add("echo_ocarina", [
    "................",
    "................",
    "................",
    "......TTTTT.....",
    "....TTTTTTTTT...",
    "...TTDTTDTTTTTT.",
    "..TTTTTTTTTDTTTM",
    "..TTDTTTTTTTTTMM",
    "..TTTTTTDTTTTTM.",
    "...TTTTTTTTTTT..",
    "....TTTTTTTTT...",
    "......TTTTT.....",
    "................",
    "................",
    "................",
    "................"], {"T": "#2e8a96", "D": "#0d2a30", "M": "#49e6f2"}, glow="M")
add("warding_chimes", [
    "................",
    ".......SS.......",
    ".......SS.......",
    "..WWWWWWWWWWWW..",
    "...S..S..S..S...",
    "...A..A..A..A...",
    "...A..A..A..A...",
    "...A..A..A..A...",
    "...A..A..A..A...",
    "...A.....A..A...",
    "...A........A...",
    "................",
    "......CC........",
    ".....CCCC.......",
    "......CC........",
    "................"], {"S": "#c8c3b5", "W": "#e07b39", "A": "#c79bf2", "C": "#e8954e"})
add("humbling_horn", [
    "................",
    "................",
    ".............HH.",
    "...........HHHH.",
    "..........HHHH..",
    ".........HHHH...",
    "........HHHH....",
    "....BBBHHHH.....",
    "...BBBBHHH......",
    "..BBBBBBH.......",
    "..BBBB..........",
    "..BBB...........",
    "...B............",
    "................",
    "................",
    "................"], {"H": "#d8cdb0", "B": "#e07b39"})
add("corrupted_seeds", [
    "................",
    "................",
    "................",
    "......P.........",
    ".....PSP....V...",
    "......P....VSV..",
    "...V........V...",
    "..VSV...P.......",
    "...V...PSP......",
    ".......SSS......",
    "........P..P....",
    "....P.....PSP...",
    "...PSP.....P....",
    "....P...........",
    "................",
    "................"], {"P": "#5a2f6a", "S": "#b04ae0", "V": "#3f7a4a"}, glow="S")
add("soul_harvester", [
    "................",
    "................",
    "....KK....KK....",
    "...K..K..K..K...",
    "...K...KK...K...",
    "....K.BBBB.K....",
    ".....BBWWBB.....",
    ".....BWWWWB.....",
    ".....BWWWWB.....",
    ".....BBWWBB.....",
    "....K.BBBB.K....",
    "...K...KK...K...",
    "...K..K..K..K...",
    "....KK....KK....",
    "................",
    "................"], {"K": "#16343c", "B": "#3fb8e0", "W": "#e0fbff"}, glow="BW")
add("corrupted_beacon", [
    "................",
    "......SSSS......",
    ".....SWWWWS.....",
    ".....SWWWWS.....",
    "......SSSS......",
    ".....GGGGGG.....",
    "....GG.SS.GG....",
    "....G.SWWS.G....",
    "....G.SWWS.G....",
    "....GG.SS.GG....",
    "....GGGGGGGG....",
    "...OOOOOOOOOO...",
    "...OPPOOOOPPO...",
    "...OOOOOOOOOO...",
    "................",
    "................"], {"S": "#3fb8e0", "W": "#e0fbff", "G": "#8fd0dc", "O": "#2a1a3a", "P": "#b04ae0"}, glow="SWP")

# ---------------------------------------------------------------------------------------------- armor icons
ARMOR_SHAPES = {
    "helmet": [
        "................",
        "................",
        "................",
        "....AAAAAAAA....",
        "...ABBBBBBBBA...",
        "...ABCCCCCCBA...",
        "...ABC....CBA...",
        "...AB......BA...",
        "...AB......BA...",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................"],
    "chestplate": [
        "................",
        "..AAA......AAA..",
        "..ABBA....ABBA..",
        "..ABBBAAAABBBA..",
        "..ABBBBBBBBBBA..",
        "...ABBCBBCBBA...",
        "....ABBBBBBA....",
        "....ABCBBCBA....",
        "....ABBBBBBA....",
        "....ABBCCBBA....",
        "....ABBBBBBA....",
        "....AAAAAAAA....",
        "................",
        "................",
        "................",
        "................"],
    "leggings": [
        "................",
        "................",
        "...AAAAAAAAAA...",
        "...ABBBCCBBBA...",
        "...ABBBBBBBBA...",
        "...ABBBAABBBA...",
        "...ABBA..ABBA...",
        "...ABBA..ABBA...",
        "...ABCA..ACBA...",
        "...ABBA..ABBA...",
        "...ABBA..ABBA...",
        "...AAAA..AAAA...",
        "................",
        "................",
        "................",
        "................"],
    "boots": [
        "................",
        "................",
        "................",
        "................",
        "................",
        "...AAA....AAA...",
        "...ABA....ABA...",
        "...ABA....ABA...",
        "...ACA....ACA...",
        "..ABBA....ABBA..",
        ".ABBBA....ABBBA.",
        ".AAAAA....AAAAA.",
        "................",
        "................",
        "................",
        "................"],
}
ARMOR_PALETTES = {
    "sifter": {"A": "#8e3b22", "B": "#d27a42", "C": "#6aa84a"},
    "mad_sifter": {"A": "#4a1018", "B": "#b02a2a", "C": "#ffb03a"},
}
for set_name, pal in ARMOR_PALETTES.items():
    for piece, rows in ARMOR_SHAPES.items():
        add(f"{set_name}_{piece}", rows, pal)


# ---------------------------------------------------------------------------------------------- armor layers
def armor_layers(name, base, accent, trim):
    """Paints the 64x32 humanoid armor layers (layer 1: helmet, chest, arms, boots; layer 2: leggings)."""
    import random
    rnd = random.Random(sum(map(ord, name)))
    b = hex2rgb(base)
    a = hex2rgb(accent)
    t = hex2rgb(trim)

    def box(img, u, v, w, h, d, pattern):
        regions = [(u + d, v, w, d), (u + d + w, v, w, d), (u, v + d, d, h), (u + d, v + d, w, h),
                   (u + d + w, v + d, d, h), (u + 2 * d + w, v + d, w, h)]
        for (ru, rv, rw, rh) in regions:
            for y in range(rh):
                for x in range(rw):
                    c = pattern(x, y, rw, rh)
                    f = 1 + rnd.uniform(-0.06, 0.06)
                    if x in (0, rw - 1) or y in (0, rh - 1):
                        f *= 0.8
                    img.putpixel((ru + x, rv + y), shade(c, f) + (255,))

    def plates(x, y, w, h):
        if y % 4 == 3:
            return shade(b, 0.7)
        if (x + y) % 7 == 0:
            return a
        return b

    def edge_trim(x, y, w, h):
        if y == h - 1 or y == 0:
            return t
        return plates(x, y, w, h)

    layer1 = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    box(layer1, 0, 0, 8, 8, 8, edge_trim)      # helmet
    box(layer1, 16, 16, 8, 12, 4, plates)      # chest
    box(layer1, 40, 16, 4, 12, 4, edge_trim)   # arms
    box(layer1, 0, 16, 4, 12, 4, edge_trim)    # boots (legs area of layer 1)
    # clear helmet face opening
    for y in range(11, 16):
        for x in range(9, 15):
            layer1.putpixel((x, y), (0, 0, 0, 0))
    layer1.save(out_path("textures", "models", "armor", f"{name}_layer_1.png"))
    layer2 = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    box(layer2, 16, 16, 8, 12, 4, plates)      # leggings waist
    box(layer2, 0, 16, 4, 12, 4, plates)       # legs
    layer2.save(out_path("textures", "models", "armor", f"{name}_layer_2.png"))


EFFECTS = {
    "soul_burn": ([
        "..................",
        "........C.........",
        ".......CC.........",
        ".......CCC........",
        "......CCWC....C...",
        "...C..CCWCC..CC...",
        "...CC.CWWWC.CCC...",
        "...CCCCWWWCCCWC...",
        "....CCWWWWWCWWC...",
        "....CWWWBWWWWC....",
        "....CWWBBBWWWC....",
        "....CCWBBBBWCC....",
        ".....CWBBBBWC.....",
        ".....CCWBBWCC.....",
        "......CCWWCC......",
        ".......CCCC.......",
        "..................",
        ".................."], {"C": "#2a8fc4", "W": "#5fd3ff", "B": "#d8f8ff"}),
    "echo_ward": ([
        "..................",
        "....TTTTTTTTTT....",
        "...TDDDDDDDDDDT...",
        "...TDCCCCCCCCDT...",
        "...TDC......CDT...",
        "...TDC.TTTT.CDT...",
        "...TDC.TCCT.CDT...",
        "...TDC.TCCT.CDT...",
        "...TDC.TTTT.CDT...",
        "...TDC......CDT...",
        "....TDC....CDT....",
        "....TDCC..CCDT....",
        ".....TDCCCCDT.....",
        "......TDDDDT......",
        ".......TTTT.......",
        "..................",
        "..................",
        ".................."], {"T": "#0d2a30", "D": "#1f6f78", "C": "#49e6f2"}),
    "enraged": ([
        "..................",
        "..R...........R...",
        "..RR.........RR...",
        "..RRR.......RRR...",
        "...RRR.....RRR....",
        "....RRRRRRRRR.....",
        "...RRYRRRRRYRR....",
        "...RRYYRRRYYRR....",
        "...RRRRRRRRRRR....",
        "...RRRRRRRRRRR....",
        "....RRWRWRWRR.....",
        "....RRRRRRRRR.....",
        ".....RRRRRRR......",
        "......RRRRR.......",
        "..................",
        "..................",
        "..................",
        ".................."], {"R": "#c81e2a", "Y": "#ffd23a", "W": "#f0ecd8"}),
}


def effect_icon(rows, palette):
    img = new(18, 18)
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch not in ". ":
                img.putpixel((x, y), hex2rgb(palette[ch]) + (255,))
    return img


def main():
    for name, (rows, pal, kw) in ICONS.items():
        save(icon(rows, pal, **kw), I, name + ".png")
    for name, (rows, pal) in EFFECTS.items():
        save(effect_icon(rows, pal), "textures/mob_effect", name + ".png")
    armor_layers("sifter", "#d27a42", "#6aa84a", "#8e3b22")
    armor_layers("mad_sifter", "#b02a2a", "#ffb03a", "#4a1018")


if __name__ == "__main__":
    main()
