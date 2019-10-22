<img src="https://www.dropbox.com/s/k9wxvebpe5jggp1/Slide1.png?raw=1" height=200 align="center"> 
# SiPAS-Profiler
 
## Authors

Jun Xu (junxu1048@genetics.ac.cn)<sup> 1,2 </sup>, Jing Wang (jing.wang@genetics.ac.cn)<sup> 1 </sup>, Fei Lu (flu@genetics.ac.cn)<sup> 1,3 </sup>

## Overview

Simplified Poly(A) Anchored Sequencing (SiPAS) is a custom 3'RNA-seq method to enhance population transcriptomic studies in plants. Just as the full name of this technology, SiPAS simplify the current tech which are successful in gauging gene expression in cultivated mammal cells. That means the hinders caused by cell wall and complex genome in pants can be solved perfectly.
To sum up, it is has three significant improvement:

#### New Feature!

     1. On the premise of ensuring accuracy, it minimizes the labor and cost for library construction.

     2. Even in plants, it can achieve self-stability under the low sampling reads number.

     3. When sampling data number reduces to one tenth, high accuracy can be also verfied by Truseq. 
     
     
     
At the same time, we launch the corresponding pipeline which can achieve analysis through one click. Using raw of RNA-seq, gene expression table of a large population can be get immediatly.
 
 #### Basic Workflow
 
```sh
1. Parse samples
```
```sh
2. Align to genome
```
```sh
3. Quantitate gene expression
```
```sh
4. Output count table
```
 
 
 
 
 #### Options
 
 T0 start your analysis, you need to provide some parameter files. There also some options you can choose.
 
 #####  1.Parameter File
 
 ```sh
SiPAS-Profiler (SPR)
Author: Jun Xu, Fei Lu
Email: junxu1048@genetic.ac.cn; flu@genetics.ac.cn
Homepage: http://plantgeneticslab.weebly.com/
#This program is used to profile 3'RNA-seq experiment, to quantify expression level of genes.
#The usage is java -Xms10g -Xmx20g TEP.jar parameters_TEP.txt > log.txt &
#Please keep the order of following parameters
#The mode of alignment. PE or SE. The default is PE mode
PE
#The directory of reference genome index
/home/genome
#The SampleInformation file (with header), the format is Taxa\tBarcode\tPlateName\tFastqPath 
/home/SampleInformation.txt
#The gene annotation file (GTF format)
/home/Zea_mays.AGPv4.38.modified.gtf
#The path of STAR alignment software
/home/software/STAR-2.5.3a/bin/MacOSX_x86_64/STAR
#The directory of output
/home/outputDir
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

