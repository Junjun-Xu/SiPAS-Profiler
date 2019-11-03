<img src="https://www.dropbox.com/s/k9wxvebpe5jggp1/Slide1.png?raw=1" height=300 align="center"> 


# SiPAS-Profiler
 
## Authors

Jun Xu (junxu1048@genetics.ac.cn)<sup> 1,2 </sup>, Jing Wang (jing.wang@genetics.ac.cn)<sup> 1 </sup>, Fei Lu (flu@genetics.ac.cn)<sup> 1,3 </sup>

## Overview

Simplified Poly(A) Anchored Sequencing (SiPAS) is a custom 3'RNA-seq method to enhance population transcriptomic studies in plants. Just as the full name of this technology, SiPAS simplify the current tech which are successful in gauging gene expression in cultivated mammal cells. That means the hinders caused by cell wall and complex genome in pants can be solved perfectly.
To sum up, it is has three significant improvement:

#### New Feature!

     1. On the premise of ensuring accuracy, it minimizes the labor and cost for library construction.

     2. Even in plants, it can achieve self-stability under the low sampling reads number.

     3. When sampling data number reduces to one tenth of Truseq, high accuracy can be also verfied. 
     
     
     
At the same time, we launch the corresponding pipeline which can achieve analysis through one click. Using raw RNA-seq data, gene expression table of a large population size can get immediately.


 #### Basic Workflow
 
```sh
1. Parse samples
```
```sh
2. Make index of reference
```
```sh
3. Align to genome
```
```sh
4. Quantitate gene expression
```
```sh
5. Output count table
```
 
 
 ### Dependence

#### Software

```sh
STAR
```
```sh
HTSeq
```
 
 #### Files
 
 To start your analysis, you need to provide some parameter files. There also some options you can choose.
 
 #####  1.Parameter File
 
 ```sh
SiPAS-Profiler (SPR)
Author: Jun Xu, Fei Lu
Email: junxu1048@genetic.ac.cn;flu@genetics.ac.cn
Homepage: http://plantgeneticslab.weebly.com/
#This program is used to profile 3'RNA-seq experiment, to quantify expression level of genes.
#The usage is java -Xms10g -Xmx20g TEP.jar parameters_TEP.txt > log.txt &
#Please keep the order of following parameters
#The mode of alignment. PE or SE. The default is PE mode
PE
#The multimap number.If the value is 10. It means that if the read map to more than 10 position, it will be discarded. Default is 2.
2
#The mismatch rate number. If this value is more than set value, this read will be discard.Defult is 0.1.
0.1
#The minimun number of match. 
80
#The directory of output
/home/outputDirS
#The gene annotation file (GTF format)
/home/wheat.gff
#The gene reference file (fa format)
/home/wheat.fa
#The path of STAR alignment software
/home/STAR-2.6.1c/bin/Linux_x86_64
#The directory of reference genome index
/home/genome
#The SampleInformation file (with header), the format is Taxa\tBarcode\tPlateName\tFastqPath 
/home/SampleInformation.txt
```

##### 2.Sample Information File
```sh
Taxa	Barcode	PlateName	FastqPath
PL_BC1	TGAACACG	Plate1	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC2	CGTTGTCA	Plate2	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC3	TGAATCAG	Plate3	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC4	TTCGACTG	Plate4	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC5	CTTAGTTG	Plate5	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC6	CGTGCTGA	Plate6	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC7	CCTCAAGC	Plate7	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC8	TGAGGACT	Plate8	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC9	CGCGTTAT	Plate9	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC10	CGTTTCAT	Plate10	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC11	GCATAGTC	Plate11	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC12	TGGCTCTA	Plate12	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC13	CAAGGAAG	Plate13	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC14	CAGTACCT	Plate14	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC15	CGACTTGT	Plate15	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC16	ATGCCTCA	Plate16	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC17	TCTCAGAA	Plate17	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC18	CTTCGTCT	Plate18	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC19	TTCACATG	Plate19	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC20	AAAGCGAG	Plate20	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC21	CGCGAATC	Plate21	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC22	TAGAGATC	Plate22	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC23	CCCAAACA	Plate23	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
PL_BC24	TTGGAAAC	Plate24	/Users/xujun/Desktop/TEP/fastq/s1116-1-L2_1.clean.fq
```

### Additional Features

#### testInfor.jar

##### Result
When we construct libraries in HTS plate, the uneven output of libraries often occurs. In order to get the relatively consistent library output, the test data is usually used to assist. The result is as a reference to guide the large-scale sequencing. The testInfor.jar file is a new pipeline to parse the information in the data. The following are the statistical results.


##### 1.ratioTable.txt

From this file you can get the reads number, barcode of each sample (Rawdata) and the degree of uniformity between samples in library. This can be as the conduct for sample mixing within the library.

```sh
20190724S1-1	Barcode	Reads number	Ratio
A01	TGAACACG	809092	2.38%
A02	CGTTGTCA	742120	2.18%
A03	TGAATCAG	745732	2.19%
A04	TTCGACTG	288972	0.85%
A05	CTTAGTTG	461020	1.36%
A06	CGTGCTGA	393824	1.16%
A07	CCTCAAGC	77632	0.23%
A08	TGAGGACT	25560	0.08%
A09	CGCGTTAT	634040	1.86%
A10	CGTTTCAT	278692	0.82%
A11	GCATAGTC	241788	0.71%
A12	TGGCTCTA	140692	0.41%
B01	CAAGGAAG	188628	0.55%
B02	CAGTACCT	372328	1.09%
B03	CGACTTGT	840972	2.47%
B04	ATGCCTCA	379772	1.12%
B05	TCTCAGAA	324532	0.95%
B06	CTTCGTCT	388492	1.14%
B07	TTCACATG	68348	0.20%
B08	AAAGCGAG	36428	0.11%
B09	CGCGAATC	394444	1.16%
B10	TAGAGATC	423768	1.25%
B11	CCCAAACA	766728	2.25%
B12	TTGGAAAC	177076	0.52%
```
##### 2.baseTable.txt

Visually view the amount of data from the size of the file.
```sh
20190724S1-1	Reads number	Base	Mbase
A01	809092	242727600	81
A02	742120	222636000	65
A03	745732	223719600	65
A04	288972	86691600	26
A05	461020	138306000	41
A06	393824	118147200	35
A07	77632	23289600	8
A08	25560	7668000	4
A09	634040	190212000	55
A10	278692	83607600	25
A11	241788	72536400	22
A12	140692	42207600	13
B01	188628	56588400	18
B02	372328	111698400	33
B03	840972	252291600	81
B04	379772	113931600	34
B05	324532	97359600	29
B06	388492	116547600	35
B07	68348	20504400	7
B08	36428	10928400	5
B09	394444	118333200	35
B10	423768	127130400	38
B11	766728	230018400	81
B12	177076	53122800	17
```
##### 3.libraryOutput.txt

It can be used as an important basis for library mixing.
```sh
library	clean data(Gbase)	raw data(Gbase)
20190724S1-1	791483100	839794800	
20190724S1-2	916298400	987440100	
20190724S1-4	795152400	862199400	
20190724S1-5	817035000	868636200	
20190724S1-6	1659344400	1770141600	
20190724S2-10	659848500	715755000	
20190724S2-11	680722200	726794100	
20190724S2-13	850248600	913733100	
```

##### Usage

The use of pipeline is also very simple. For example:
java -jar testInfor.jar /Users/home/barcode.txt /Users/home/inputFileDirS

The first parameter "/Users/home/barcode.txt" is the path of barcode file which is used to parse samples and print this information in ratioTable.txt file. The second is the the path of the input directory. Under this directory, it must contain the folder of Rawdata and Cleandata which are both needed in the following analysis.
