<img src="https://www.dropbox.com/s/k9wxvebpe5jggp1/Slide1.png?raw=1" height=300 align="center"> 


# SiPAS-Profiler
 
## Authors

Jun Xu (junxu1048@genetics.ac.cn)<sup> 1,2 </sup>, Song Xu (sxu@genetics.ac.cn)<sup> 1,2</sup>, Fei Lu (flu@genetics.ac.cn)<sup> 1,3 </sup>
## Affiliations

<sup> 1 </sup>  State Key Laboratory of Plant Cell and Chromosome Engineering , Institute of Genetics and Developmental Biology, The Innovative Academy of Seed Design, Chinese Academy of Sciences
 
<sup> 2 </sup>  University of Chinese Academy of Sciences

<sup> 3 </sup>  CAS-JIC Centre of Excellence for Plant and Microbial Science (CEPAMS), Shanghai Institutes for Biological Sciences, Chinese Academy of Sciences

## Overview

Simplified Poly(A) Anchored Sequencing (SiPAS) is a custom 3'RNA-seq method to boost population-level transcriptomic studies in plants. SiPAS simplifies current 3'RNA-seq approaches which are successful in quantifying gene expression in cultivated mammal cells. Also, it uses anchored oligo(dT) primers to obtain longer reads for accurate sequence alignment and gene expression quantification. SiPAS has three significant improvement:

#### New Feature!

     1. On the premise of high accuracy, it minimizes the labor and cost for library construction.

     2. It achieves high reproducibility -- only 5 million reads are needed to have a correlation coefficient of 0.94 between two replicates while being performed to quantify expresseon level of 12k genes in bread wheat.
     
     3. SiPAS is universal. Given its success in hexaploid wheat, which has the largest and most complex genome of crops, we believe it can be used widely in many other plants.   
     
     
     
At the same time, we launch the bioinformatic pipeline of SiPAS (SiPAS-Profiler), which performs gene expressino quantification through SIPAS data. Using raw RNA-seq data, gene expression table of a large population can be obtained immediately.


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
 
 
#### Dependence

```sh
STAR
```
```sh
HTSeq
```
 
#### Usage
 
 To start your analysis, you need to provide some parameter files. There also some options you can choose.
 
 #####  1.Parameter File
 
 ```sh
SiPAS-Profiler
Author: Jun Xu, Fei Lu
Email: junxu1048@genetics.ac.cn; flu@genetics.ac.cn
Homepage: http://plantgeneticslab.weebly.com/
#This program is used for gene expression profiling from Simplified Poly(A) Anchored Sequencing (SiPAS)
#The usage is java -Xms10g -Xmx20g SiPAS-Profiler.jar parameters_SiPAS.txt > log.txt &
#Please keep the order of following parameters
#Mode of alignment. PE or SE. The default is PE mode
PE
#Number of loci of multiple alignment. If the value is 10, it means that a read mapping to more than 10 position will be discarded. The default is 2
2
#The rate of mismatch. If this value is greater than set value, this read will be discard. The defult is 0.1
0.1
#The minimun number of match
80
#The SampleInformation file (with header), the format is Taxa\tBarcode\tPlateName\tFastqPath 
/Users/junxu/Documents/analysisL/softwareTest/sipas/input/SampleInformation.txt
#The directory of output
/Users/junxu/Documents/analysisL/softwareTest/sipas/output
#The gene annotation file (GTF format)
/Users/junxu/Documents/database/wheat/gene/v1.1/wheat_v1.1_Lulab.gtf
#The gene reference file (fa format)
/Users/junxu/Documents/database/wheat/reference/v1.0/ABD/abd_iwgscV1.fa
#The path of STAR alignment software
/Users/junxu/Software/STAR-2.5.4b/bin/MacOSX_x86_64/star
```

##### 2.Sample Information File
```sh
Taxa    Plate_ID    Barcode    FastqPath_R1    FastqPath_R2
PL-BC1    P1    TGAACACG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC2    P1    CGTTGTCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC3    P1    TGAATCAG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC4    P1    TTCGACTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC5    P1    CTTAGTTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC6    P1    CGTGCTGA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC7    P1    CCTCAAGC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC8    P1    TGAGGACT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC9    P1    CGCGTTAT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC10    P1    CGTTTCAT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC11    P1    GCATAGTC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC12    P1    TGGCTCTA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC13    P1    CAAGGAAG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC14    P1    CAGTACCT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC15    P1    CGACTTGT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC16    P1    ATGCCTCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC17    P1    TCTCAGAA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC18    P1    CTTCGTCT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC19    P1    TTCACATG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC20    P1    AAAGCGAG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC21    P1    CGCGAATC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC22    P1    TAGAGATC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC23    P1    CCCAAACA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC24    P1    TTGGAAAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC25    P1    AGACTCGC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC26    P1    TTTTACCG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC27    P1    GCTAACGG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC28    P1    CATAACCC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC29    P1    TTCCCGCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC30    P1    AACAGTTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC31    P1    TAGCTAGC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC32    P1    GCAACTAG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC33    P1    GGTTTCGC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC34    P1    GAGTTACT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC35    P1    TCCAAGTC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC36    P1    TAGCATAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC37    P1    ATCTAGGA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC38    P1    ATCATGTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC39    P1    TCTCTAGT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC40    P1    AGGAAGAA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC41    P1    ACGACCTC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC42    P1    ACACGCCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC43    P1    GTCAAGGT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC44    P1    CTCCCCTT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC45    P1    CGCAAAAT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC46    P1    GTTAGCGT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC47    P1    CCGGTGAA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC48    P1    ATTGTCCT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC49    P1    ACTGACTT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC50    P1    CGAGTAAA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC51    P1    TTACCGTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC52    P1    CCAGATAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC53    P1    GGTGATCT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC54    P1    TTTCTCCC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC55    P1    GACTCCCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC56    P1    CTTTCCCT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC57    P1    CTACTGAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC58    P1    GTGGGACT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC59    P1    CTAGCATC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC60    P1    TATCGGTC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC61    P1    TGCAGCTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC62    P1    CAGGCGTA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC63    P1    TGCTTAAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC64    P1    GTGTACAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC65    P1    TTGCGGAT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC66    P1    TCATTGGA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC67    P1    GTACCCAC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC68    P1    GAATTGTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC69    P1    CAGTTCGG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC70    P1    AGATGCTA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC71    P1    ATTGTGGC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC72    P1    ATGTAACC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC73    P1    CATGCCTT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC74    P1    CCTCTTAG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC75    P1    TACGAGCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC76    P1    GGAAATTG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC77    P1    GCAAGACG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC78    P1    ACTTTGCG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC79    P1    GTGTGCTA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC80    P1    TGTTACGT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC81    P1    CTGAGACG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC82    P1    CCAGGCAA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC83    P1    GTCGCACA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC84    P1    GTCGACGA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC85    P1    GCCATTGG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC86    P1    GAGACCGA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC87    P1    GCGCCTTA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC88    P1    TTCAGTCC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC89    P1    CTTAGGCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC90    P1    TCCTGTCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC91    P1    AAGAGAGG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC92    P1    TCGTTAAG    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC93    P1    TGTCCCCA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC94    P1    CACATTCC    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC95    P1    CGGTGATA    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
PL-BC96    P1    ACGACTGT    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R1.fq.gz    /Users/junxu/Documents/analysisL/softwareTest/sipas/input/sub1-1_R2.fq.gz
```

##### 3.Run SiPAS from command line
java -Xms10g -Xmx20g SiPAS-Profiler.jar parameters_SiPAS.txt > log.txt


## Additional Features

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

##### Note

The generated files will not be used for subsequent analysis. So all intermediate files will be deleted at the end, leaving only the final result file.

