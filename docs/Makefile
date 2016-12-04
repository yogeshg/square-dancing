all: latex report presentation

doc = draft

latex :
	pandoc --toc --to latex $(doc).md -o $(doc).tex

report :
	pandoc --toc --to latex $(doc).md -o $(doc).pdf

presentation :
	pandoc --toc --to beamer $(doc).md -o $(doc)-beamer.pdf


