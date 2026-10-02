"""Regenerates every texture and entity model of the mod."""
import gen_blocks
import gen_entities
import gen_items

if __name__ == "__main__":
    gen_blocks.main()
    gen_entities.main()
    gen_items.main()
    print("all assets generated")
