# spjok

A tiny Python command-line starter project. It prints a greeting and includes a small test suite, so it can be extended into a larger application.

## Requirements

- Python 3.9 or newer

## Run

From the repository root:

```bash
PYTHONPATH=src python -m spjok
PYTHONPATH=src python -m spjok Ada
```

For an editable installation with the `spjok` command:

```bash
python -m pip install -e .
spjok Ada
```

## Test

```bash
PYTHONPATH=src python -m unittest discover -s tests -v
```
