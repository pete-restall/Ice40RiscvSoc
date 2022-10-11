from setuptools import setup, find_packages

setup(
	name="msf-bootloader",
	version="0.1.0",
	description="",
	author="Pete Restall",
	author_email="pete@restall.net",
	url="https://github.com/lophtware/MsfFrequencyReference",
	packages=find_packages(include=["src"]),
	install_requires=["wheel", "myhdl"],
	setup_requires=["pytest-runner"],
	tests_require=["pytest"])
