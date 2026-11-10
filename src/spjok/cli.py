"""Command-line interface for spjok."""

import argparse


def main() -> None:
    parser = argparse.ArgumentParser(description="A tiny Python starter project.")
    parser.add_argument("name", nargs="?", default="world", help="who to greet")
    args = parser.parse_args()
    print(f"Hello, {args.name}!")


if __name__ == "__main__":
    main()
