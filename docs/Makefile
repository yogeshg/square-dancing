all: latex report beamer presentation

doc = tournament-analysis
pres = tournament-analysis-beamer

latex :
	pandoc --toc --to latex $(doc).md -o $(doc).tex

report :
	pandoc --toc --to latex $(doc).md -o $(doc).pdf

beamer:
	pandoc --toc --to beamer $(pres).md -o $(pres).tex

presentation :
	pandoc --toc --to beamer $(pres).md -o $(pres).pdf


