import io
import unittest
from contextlib import redirect_stdout
from unittest.mock import patch

from spjok.cli import main


class CliTests(unittest.TestCase):
    def test_default_greeting(self):
        output = io.StringIO()
        with patch("sys.argv", ["spjok"]), redirect_stdout(output):
            main()
        self.assertEqual(output.getvalue(), "Hello, world!\n")

    def test_custom_name(self):
        output = io.StringIO()
        with patch("sys.argv", ["spjok", "Manus"]), redirect_stdout(output):
            main()
        self.assertEqual(output.getvalue(), "Hello, Manus!\n")


if __name__ == "__main__":
    unittest.main()
