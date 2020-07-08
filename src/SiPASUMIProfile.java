/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author xujun
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import format.table.RowTable;
import gnu.trove.list.array.TIntArrayList;
import htsjdk.samtools.*;
import utils.Benchmark;
import utils.IOUtils;
import utils.PStringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

/**
 *
 * @author xujun
 */
public class SiPASUMIProfile {
    // The mode of alignment. PE or SE. The default is PE mode.
    String alignType=null;
    //The multimap number.If the value is 10. It means that if the read map to more than 10 position, it will be discarded. Default is 2.
    int multiMapN= 0;
    //The mismatch rate number. If this value is more than set value, this read will be discared.Defult is 0.1.
    float mismatchRate= 0;
    //The minize number of match.
    int minNMatch = 0;
    //The SampleInformation file (with header), the format is Taxa\tBarcode\tPlateName\tFastqPath
    String sampleInformationFileS = null;
    //The directory of output
    String outputDirS = null;
    //The gene annotation file (GTF format)
    String geneAnnotationFileS = null;
    //The gene reference file (fa format)
    String referenceGenomeFileS = null;
    //The path of STAR alignment software
    String starPath = null;

    List<String> fqFileSListR1 = null;
    HashMap<String, String> fqR1R2Map = new HashMap();

    List<String>[] barcodeLists = null;

    HashMap<String, String>[] barcodeTaxaMaps = null;

    List<String>[] taxaLists = null;

    List<String>[] barcodeLengthLists = null;

    List<String> allTaxaList = new ArrayList<String>();

    TIntArrayList[]  barcodeLengths = null;

    String [] names=null;



    String[] subDirS = {"subFastqs", "sams", "geneCount", "countTable"};

    public SiPASUMIProfile(String arg) {
        this.parseParameters(arg);
        this.processTaxaAndBarcode();
//        starLib=new File("/data1/home/junxu/eQTL/phenotype/starLib1.1").getAbsoluteFile();
        File starLib=new File(this.outputDirS,"starLib").getAbsoluteFile();
        if(!starLib.exists()){
            this.mkIndexOfReference();
        }else{
            File Genome=new File(this.outputDirS,"starLib/Genome").getAbsoluteFile();
            if(!Genome.exists()){
                this.mkIndexOfReference();
            }
        }
        if(alignType=="SE"){
            this.SEParse();
            this.starAlignmentSE();
            this.HTSeqCountSE();
        }else{
            this.PEParse();
            this.starAlignmentPE();
            this.annotatedBAM();
            this.removeDuplicate();
            this.HTSeqCountPE();

        }
        this.countTable();
        this.countUMITable();
    }
    private void SEParse () {
        long startTimePoint = System.nanoTime();
        fqFileSListR1.parallelStream().forEach(f -> {
            int fqIndex = Collections.binarySearch(this.fqFileSListR1, f);
            String subFqDirS = new File (this.outputDirS,subDirS[0]).getAbsolutePath();
            List<String> barcodeList = barcodeLists[fqIndex];
            String[] subFqFileS = new String[barcodeList.size()];
            HashMap<String, String> btMap = barcodeTaxaMaps[fqIndex];
            TIntArrayList barcodeLength = this.barcodeLengths[fqIndex];
            Set<String> barcodeSet = btMap.keySet();
            BufferedWriter[] bws = new BufferedWriter[subFqFileS.length];
            HashMap<String, BufferedWriter> barcodeWriterMap = new HashMap<>();
            for (int i = 0; i < subFqFileS.length; i++) {
                String taxon = btMap.get(barcodeList.get(i));
                subFqFileS[i] = new File(subFqDirS, taxon+"_R2.fq").getAbsolutePath();
                bws[i] = IOUtils.getTextWriter(subFqFileS[i]);
                barcodeWriterMap.put(barcodeList.get(i), bws[i]);
            }
            try {
                BufferedReader br1 = null;
                BufferedReader br2 = null;
                String f2= fqR1R2Map.get(f);
                if (f.endsWith(".gz")) {
                    br1 = IOUtils.getTextGzipReader(f);
                    br2 = IOUtils.getTextGzipReader(f2);
                }
                else {
                    br1 = IOUtils.getTextReader(f);
                    br2 = IOUtils.getTextGzipReader(f2);
                }
                String temp = null;
                String seq = null;
                String currentBarcode = null;
                String currentUMI=null;
                BufferedWriter tw = null;
                int cnt = 0;
                int cnt2 = 0;
                while((temp = br1.readLine())!=null){
                    cnt2++;
                    seq = br1.readLine();
                    //*************************
                    //barcode needs  to have at least 2 mismathches between each other, currently at 3 mismathces between any 8-bp barcodes
                    //barcode can be redesigned to have 4-8 bp in length for even efficiency between barcdes
                    //*************************
                    currentBarcode = seq.substring(0, barcodeLength.get(0));
                    currentUMI=seq.substring(barcodeLength.get(0),barcodeLength.get(0)+10);
                    int cutIndex = 0;
                    if (barcodeSet.contains(currentBarcode)) {
                        tw = barcodeWriterMap.get(currentBarcode);
                        tw.write(br2.readLine()+"_"+currentUMI);tw.newLine();
                        tw.write(br2.readLine());tw.newLine();
                        tw.write(br2.readLine());tw.newLine();
                        tw.write(br2.readLine());tw.newLine();
                        cnt++;
                    }else {
                        br2.readLine();br2.readLine();br2.readLine();br2.readLine();
                        continue;
                    }
                    br1.readLine();br1.readLine();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(cnt).append(" out of ").append(cnt2).append(", ").append(((float)(double)cnt/cnt2)).append(" of total reads were parsed from " + f).append(" ").append(f2);
                System.out.println(sb.toString());
                for (int i = 0; i < subFqFileS.length; i++) {
                    bws[i].flush();
                    bws[i].close();
                }
                br1.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
        StringBuilder time = new StringBuilder();
        time.append("Distinguish samples according to barcode and trim the barcode.").append("Took ").append(Benchmark.getTimeSpanSeconds(startTimePoint)).append(" seconds. Memory used: ").append(Benchmark.getUsedMemoryGb()).append(" Gb");
        System.out.println(time.toString());
    }

    private void PEParse () {
        long startTimePoint = System.nanoTime();
        fqFileSListR1.stream().forEach(f -> {
            int fqIndex = Collections.binarySearch(this.fqFileSListR1, f);
            String subFqDirS = new File (this.outputDirS,subDirS[0]).getAbsolutePath();
            List<String> barcodeList = barcodeLists[fqIndex];
            String[] subFqFileS = new String[barcodeList.size()];
            HashMap<String, String> btMap = barcodeTaxaMaps[fqIndex];
            TIntArrayList barcodeLength = this.barcodeLengths[fqIndex];
            Set<String> barcodeSet = btMap.keySet();
            BufferedWriter[] bws1 = new BufferedWriter[subFqFileS.length];
            BufferedWriter[] bws2 = new BufferedWriter[subFqFileS.length];
            BufferedWriter bwE =IOUtils.getTextGzipWriter(new File(subFqDirS+"/"+f.split("/")[f.split("/").length-1].replace("fq.gz", alignType)+"_error.txt.gz").getAbsolutePath());
            HashMap<String, BufferedWriter> barcodeWriterMap1 = new HashMap<>();
            HashMap<String, BufferedWriter> barcodeWriterMap2 = new HashMap<>();
            for (int i = 0; i < subFqFileS.length; i++) {
                String taxon = btMap.get(barcodeList.get(i));
                subFqFileS[i] = new File(subFqDirS, taxon+".fq").getAbsolutePath();
                bws1[i] = IOUtils.getTextGzipWriter(new File(subFqDirS, taxon+"_R1.fq.gz").getAbsolutePath());
                bws2[i] = IOUtils.getTextGzipWriter(new File(subFqDirS, taxon+"_R2.fq.gz").getAbsolutePath());
                barcodeWriterMap1.put(barcodeList.get(i), bws1[i]);
                barcodeWriterMap2.put(barcodeList.get(i), bws2[i]);
            }
            try {
                BufferedReader br1 = null;
                BufferedReader br2 = null;
                String f2= fqR1R2Map.get(f);
                if (f.endsWith(".gz")) {
                    br1 = IOUtils.getTextGzipReader(f);
                    br2 = IOUtils.getTextGzipReader(f2);
                }
                else {
                    br1 = IOUtils.getTextReader(f);
                    br2 = IOUtils.getTextReader(f2);
                }
                String temp = null;
                String seq = null;
                String currentBarcode = null;
                String currentUMI=null;
                BufferedWriter tw1 = null;
                BufferedWriter tw2 = null;
                int cnt = 0;
                int cnt2 = 0;
                while((temp = br1.readLine())!=null){
                    cnt2++;
                    seq = br1.readLine();
                    //*************************
                    //barcode needs  to have at least 2 mismathches between each other, currently at 3 mismathces between any 8-bp barcodes
                    //barcode can be redesigned to have 4-8 bp in length for even efficiency between barcdes
                    //*************************
                    currentBarcode = seq.substring(0, barcodeLength.get(0));
                    currentUMI=seq.substring(barcodeLength.get(0),barcodeLength.get(0)+10);
                    int cutIndex = 0;
                    if (barcodeSet.contains(currentBarcode)) {
                        tw1 = barcodeWriterMap1.get(currentBarcode);
                        tw1.write(temp+"_"+currentUMI);tw1.newLine();
//                        tw1.write(seq.substring(currentBarcode.length()+currentUMI.length()+5));tw1.newLine();
                        tw1.write(seq);tw1.newLine();
                        tw1.write(br1.readLine());tw1.newLine();
//                        tw1.write(br1.readLine().substring(currentBarcode.length()+currentUMI.length()+5));tw1.newLine();
                        tw1.write(br1.readLine());tw1.newLine();
                        tw2 = barcodeWriterMap2.get(currentBarcode);
                        tw2.write(br2.readLine());tw2.newLine();
                        tw2.write(br2.readLine());tw2.newLine();
                        tw2.write(br2.readLine());tw2.newLine();
                        tw2.write(br2.readLine());tw2.newLine();
                        cnt++;
                    }
                    else {
                        bwE.write(temp);bwE.newLine();
                        bwE.write(seq);bwE.newLine();
                        bwE.write(br1.readLine());bwE.newLine();
                        bwE.write(br1.readLine());bwE.newLine();
                        br2.readLine();br2.readLine();br2.readLine();br2.readLine();
                        continue;
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append(cnt).append(" out of ").append(cnt2).append(", ").append(((float)(double)cnt/cnt2)).append(" of total reads were parsed from " + f).append(" ").append(f2);
                System.out.println(sb.toString());
                for (int i = 0; i < subFqFileS.length; i++) {
                    bws1[i].flush();bws2[i].flush();
                    bws1[i].close();bws2[i].close();
                }
                br1.close();br2.close();
                bwE.flush();bwE.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
        StringBuilder time = new StringBuilder();
        time.append("Distinguish samples according to barcode and trim the barcode.").append("Took ").append(Benchmark.getTimeSpanSeconds(startTimePoint)).append(" seconds. Memory used: ").append(Benchmark.getUsedMemoryGb()).append(" Gb");
        System.out.println(time.toString());
    }
    private void mkIndexOfReference () {
        int numCores = Runtime.getRuntime().availableProcessors();
        new File(this.outputDirS, "starLib").mkdir();
        String outputDirS = new File (this.outputDirS, "starLib").getAbsolutePath();
        try {
            StringBuilder sb = new StringBuilder(this.starPath);
            sb.append(" --runThreadN ").append(numCores).append(" --runMode genomeGenerate --genomeDir ").append(outputDirS);
            sb.append(" --sjdbGTFfile ").append(geneAnnotationFileS);
            sb.append(" --genomeFastaFiles ").append(referenceGenomeFileS);
            sb.append(" --sjdbOverhang ").append(140);
            sb.append(" --genomeChrBinNbits 17 --genomeSAsparseD 2 --limitGenomeGenerateRAM 40000000000");
            String command = sb.toString();
            System.out.println(command);
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                System.out.println(temp);
            }
            p.waitFor();
        }
        catch (Exception ee) {
            ee.printStackTrace();
        }
    }
    private void starAlignmentPE () {
        long startTimePoint = System.nanoTime();
        String subFqDirS = new File (this.outputDirS, subDirS[0]).getAbsolutePath();
        File[] fs = new File(subFqDirS).listFiles();
        List<File> fList = new ArrayList(Arrays.asList());
        fs = IOUtils.listFilesEndsWith(fs, ".fq.gz");
        HashSet<String> nameSet = new HashSet();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isHidden()) continue;
            int le=fs[i].getName().split("_").length-1;
            nameSet.add(fs[i].getName().replace(fs[i].getName().split("_")[le], ""));
        }
        int numCores = Runtime.getRuntime().availableProcessors();
        int count=0;HashSet <String> nameSet1 = new HashSet();
        names = nameSet.toArray(new String[nameSet.size()]);
        for(int i=0;i<names.length;i++){
            if((i+1)/2==count && i!=nameSet.size()-1){//实现了同时跑10个HTSeq
                nameSet1.add(names[i]);
            }else{
                nameSet1.add(names[i]);
                nameSet1.parallelStream().forEach(f -> {
                    String infile1 = new File (subFqDirS,f+"R1.fq.gz").getAbsolutePath();
                    String infile2 = new File (subFqDirS,f+"R2.fq.gz").getAbsolutePath();
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.starPath).append(" --runThreadN ").append(numCores);
                    sb.append(" --genomeDir ").append(new File(this.outputDirS,"starLib").getAbsolutePath());
                    sb.append(" --genomeLoad LoadAndKeep");
                    sb.append(" --readFilesCommand zcat");
                    sb.append(" --readFilesIn ").append(infile1+" "+infile2);
                    sb.append(" --outFileNamePrefix ").append(new File(new File(this.outputDirS, subDirS[1]).getAbsolutePath(), f)
                            .getAbsolutePath()).append(" --outFilterMultimapNmax ").append(this.multiMapN);
                    sb.append(" --outFilterMismatchNoverLmax ").append(this.mismatchRate)
                            .append(" --outFilterIntronMotifs RemoveNoncanonicalUnannotated ");
                    sb.append(" --outSAMtype BAM Unsorted");
                    sb.append(" --outFilterScoreMinOverLread 0 --outFilterMatchNminOverLread 0 --outFilterMatchNmin ").append(this.minNMatch);
                    String command = sb.toString();
                    System.out.println(command);
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process p = rt.exec(command);
                        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String temp = null;
                        while ((temp = br.readLine()) != null) {
                            System.out.println(temp);
                        }
                        p.waitFor();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Finished "+f);
                });
                count++;
                nameSet1.clear();
            }
        }
        StringBuilder time = new StringBuilder();
        System.out.println(time.toString());

    }
    private void starAlignmentSE () {
        long startTimePoint = System.nanoTime();
        String subFqDirS = new File (this.outputDirS, subDirS[0]).getAbsolutePath();
        File[] fs = new File(subFqDirS).listFiles();
        List<File> fList = new ArrayList(Arrays.asList());
        fs = IOUtils.listFilesEndsWith(fs, ".fq");
        HashSet<String> nameSet = new HashSet();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isHidden()) continue;
            nameSet.add(fs[i].getName().replace(fs[i].getName().split("_")[2],""));
        }
        int numCores = Runtime.getRuntime().availableProcessors();
        nameSet.stream().forEach(f -> {
            String infile2 = new File (subFqDirS,f+"R2.fq").getAbsolutePath();
            StringBuilder sb = new StringBuilder();
            sb.append(this.starPath).append(" --runThreadN ").append(numCores);
            sb.append(" --genomeDir ").append(new File(this.outputDirS,"starLib").getAbsolutePath());
            sb.append(" --genomeLoad LoadAndKeep");
            sb.append(" --readFilesIn ").append(infile2);
            sb.append(" --outFileNamePrefix ").append(new File(new File(this.outputDirS, subDirS[1]).getAbsolutePath(), f)
                    .getAbsolutePath()).append(" --outFilterMultimapNmax ").append(this.multiMapN);
            sb.append(" --outFilterMismatchNoverLmax ").append(this.mismatchRate)
                    .append(" --outFilterIntronMotifs RemoveNoncanonicalUnannotated ");
            sb.append(" --outSAMtype BAM SortedByCoordinate");
            sb.append(" --outFilterScoreMinOverLread 0 --outFilterMatchNminOverLread 0 --outFilterMatchNmin ").append(this.minNMatch);
            String command = sb.toString();
            System.out.println(command);
            try {
                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    System.out.println(temp);
                }
                p.waitFor();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Finished"+f);
        });
        StringBuilder time = new StringBuilder();
        System.out.println(time.toString());

    }
    public void annotatedBAM(){
        String inputDirS = new File (this.outputDirS, subDirS[1]).getAbsolutePath();
        String fqDirS=new File (this.outputDirS, subDirS[0]).getAbsolutePath();
        File[] fs = new File(inputDirS).listFiles();
        fs = IOUtils.listFilesEndsWith(fs, "Aligned.out.bam");
        List<File> fList = Arrays.asList(fs);
        int count=0;
        List<File> fSet1 = new ArrayList(Arrays.asList());
        for(int i=0;i<fList.size();i++){
            if((i+1)/10==count && i!=fList.size()-1){//实现了n个同时跑
                fSet1.add(fList.get(i));
            }else{
                fSet1.add(fList.get(i));
                fSet1.parallelStream().forEach(f -> {
                    try{
                        BufferedReader br =IOUtils.getTextGzipReader(fqDirS+"/"+f.getName().replace("Aligned.out.bam", "R1.fq.gz"));
                        HashMap<String,String>[] hm = new HashMap[11];
                        HashSet<String>[] hs=new HashSet[11];
                        for(int j=0;j<hm.length;j++){
                            hm[j]=new HashMap();
                            hs[j]=new HashSet();
                        }
                        String temp=null;String QNAME=null;String currentUMI=null;
                        while((temp=br.readLine())!=null){
                            QNAME=temp.split(" ")[0].replace("@", "");
                            currentUMI=temp.split("_")[1];
                            int num=10-currentUMI.replaceAll("A", "").length();
                            hm[num].put(QNAME, currentUMI);
                            hs[num].add(QNAME);
                            br.readLine();br.readLine();br.readLine();
                        }
                        SamReader sr = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(f);
                        SAMFileHeader header=sr.getFileHeader();
                        SAMFileWriterFactory samWriterFactory = new SAMFileWriterFactory();
                        SAMFileWriter samWriter = samWriterFactory.makeBAMWriter(header, false, new File(inputDirS + "/"+f.getName().replace("Aligned.out.bam", "annotated.bam")));
                        SAMRecordIterator r = sr.iterator();
                        while(r.hasNext()) {
                            SAMRecord tem=r.next();
//                            System.out.print(tem.getReadName());
                            for(int j =0;j<hs.length;j++){
                                if(hs[j].contains(tem.getReadName())){
                                    tem.setAttribute("UI", hm[j].get(tem.getReadName()));
                                }
                            }
                            samWriter.addAlignment(tem);
                        }
                        samWriter.close();
                        System.out.println("Finished"+f);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                count++;
                fSet1.clear();
            }
        }
    }

    public void removeDuplicate(){
        String inputDirS = new File (this.outputDirS, subDirS[1]).getAbsolutePath();
        File[] fs = new File(inputDirS).listFiles();
        fs = IOUtils.listFilesEndsWith(fs, "annotated.bam");
        List<File> fList = Arrays.asList(fs);
        int count=0;List<File> fL1 = new ArrayList(Arrays.asList());
        for(int i=0;i<fList.size();i++){
            if((i+1)/10==count && i!=fList.size()-1){//实现了同时跑10个
                fL1.add(fList.get(i));
            }else{
                fL1.add(fList.get(i));
                fL1.parallelStream().forEach(f -> {
                    SamReader sr = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(f);
                    HashSet UMISet = new HashSet();
                    String UMI= null;
                    SAMFileHeader header=sr.getFileHeader();
                    SAMFileWriterFactory samWriterFactory = new SAMFileWriterFactory();
                    SAMFileWriter samWriter = samWriterFactory.makeBAMWriter(header, false, new File(inputDirS +"/"+ f.getName().replace("annotated.bam", "UMI.bam")));
                    SAMRecordIterator r = sr.iterator();
                    while(r.hasNext()) {
                        SAMRecord tem=r.next();
                        UMI=tem.getAttribute("UI").toString()+tem.getAlignmentStart()+tem.getAlignmentEnd();
                        if(!UMISet.contains(UMI)){
                            UMISet.add(UMI);
                            samWriter.addAlignment(tem);
                        }
                    }
                    samWriter.close();
                    System.out.println("Finished"+f);
                });
                count++;
                fL1.clear();
            }
        }
    }
    public void HTSeqCountPE(){
        String inputDirS = new File (this.outputDirS, subDirS[1]).getAbsolutePath();
        File[] fs = new File(inputDirS).listFiles();
        List<File> fList = new ArrayList(Arrays.asList());
        for(int i=0; i< fs.length;i++){
            if(fs[i].getName().contains("UMI.bam") || fs[i].getName().contains("Aligned.out.bam") ){//|| fs[i].getName().contains("annotated.bam")
                fList.add(fs[i]);
            }
        }
        int count=0;List<File> fL1 = new ArrayList(Arrays.asList());
        for(int i=0;i<fList.size();i++){
            if((i+1)/10==count && i!=fList.size()-1){//实现了同时跑10个HTSeq
                fL1.add(fList.get(i));
            }else{
                fL1.add(fList.get(i));
                fL1.parallelStream().forEach(f -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("htseq-count").append(" -f bam -m intersection-nonempty -s reverse ");
                    sb.append(f);
                    sb.append(" "+this.geneAnnotationFileS).append(" > ");
                    if(f.getName().contains("UMI.bam")){
                        sb.append(f.getName().replace("UMI.bam", "UMICount.txt"));
                    }else{
                        sb.append(f.getName().replace("Aligned.out.bam", "RawCount.txt"));
                    }
                    String command = sb.toString();
                    System.out.println(command);
                    try {
                        File dir = new File(new File (this.outputDirS,subDirS[2]).getAbsolutePath());
                        String []cmdarry ={"/bin/bash","-c",command};
                        Process p=Runtime.getRuntime().exec(cmdarry,null,dir);
                        p.waitFor();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Finished"+f);
                });
                count++;
                fL1.clear();
            }
        }
    }
    public void HTSeqCountSE(){
        String inputDirS = new File (this.outputDirS, subDirS[1]).getAbsolutePath();
        File[] fs = new File(inputDirS).listFiles();
        fs = IOUtils.listFilesEndsWith(fs, "UMI.bam");
        List<File> fList = Arrays.asList(fs);
        int count=0;List<File> fL1 = new ArrayList(Arrays.asList());
        for(int i=0;i<fList.size();i++){
            if((i+1)/10==count && i!=fList.size()-1){//实现了同时跑10个HTSeq
                fL1.add(fList.get(i));
            }else{
                fL1.add(fList.get(i));
                fL1.parallelStream().forEach(f -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("htseq-count").append(" -f bam -m intersection-nonempty -s no ");
                    sb.append(f);
                    sb.append(" "+this.geneAnnotationFileS).append(" >> ");
                    sb.append(f.getName().replace("UMI.bam", "Count.txt"));
                    String command = sb.toString();
                    System.out.println(command);
                    try {
                        File dir = new File(new File (this.outputDirS,subDirS[2]).getAbsolutePath());
                        String []cmdarry ={"/bin/bash","-c",command};
                        Process p=Runtime.getRuntime().exec(cmdarry,null,dir);
                        p.waitFor();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Finished"+f);
                });
                count++;
                fL1.clear();
            }
        }
    }
    public void countUMITable(){
        List <String> plateList=new ArrayList<>();
        List <File> [] fileList=new List[fqFileSListR1.size()];//
        for(int i=0;i<fqFileSListR1.size();i++){//
            fileList[i]=new ArrayList<File>();
        }
//        String subCountDirS = new File ("/Users/xujun/Desktop/eQTL/total/geneCount").getAbsolutePath();
        String subCountDirS = new File (this.outputDirS,subDirS[2]).getAbsolutePath();
        File[] fs = new File(subCountDirS).listFiles();
        fs = IOUtils.listFilesEndsWith(fs, "UMICount.txt");
        List<File> fList = Arrays.asList(fs);
        int length=0;
        for(int i=0;i < fList.size();i++){
            length=fList.get(i).getName().split("_").length-2;
            String plate=fList.get(i).getName().split("_")[length];
            if(!plateList.contains(plate)){
                plateList.add(plate);
            }
            fileList[plateList.indexOf(plate)].add(fList.get(i));
        }
        int geneNumber=0;String geneName=null;
        Set<String> geneSet = new HashSet<String>();
        for(int i=0;i<fileList.length;i++){
            Collections.sort(fileList[i]);
        }
        int geneNmuber=0;
        StringBuilder wc = new StringBuilder();
        wc.append("wc -l ").append(fileList[0].get(0));
        String command = wc.toString();
        System.out.println(command);
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                geneNumber=Integer.valueOf(temp.split(" ")[0])-5;
            }
            p.waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<fileList.length;i++){
            Set<String> nameSet = new HashSet<String>();
            List <String> nameList=new ArrayList<>();
            int [][] count = new int [geneNumber][fileList[i].size()];
            List<File> list = fileList[i];
            list.stream().forEach(f -> {
                String temp=null;String[] tem = null;
                try{
                    BufferedReader br = IOUtils.getTextReader(f.getAbsolutePath());
                    int rowN=0;
                    while((temp = br.readLine()) != null){
                        List<String> tList= PStringUtils.fastSplit(temp);
                        tem = tList.toArray(new String[tList.size()]);
                        if(!tem[0].startsWith("__")){
                            if(!nameSet.contains(tem[0])){
                                nameList.add(tem[0]);
                            }
                            nameSet.add(tem[0]);
//                          int index1=nameList.indexOf(tem[0]);
                            count[rowN][list.indexOf(f)]=Integer.parseInt(tem[1]);
                            rowN++;
                        }
                    }

                }
                catch (Exception ex) {
                    System.out.println(tem[0]+"\t"+geneSet.size()+"\t1234");
                    ex.printStackTrace();

                }
            });
            File subDir = new File (this.outputDirS,subDirS[3]);
            String outputFileS = new File (subDir,fileList[i].get(0).getName().split("_")[length]+"_UMI_countResult.txt").getAbsolutePath();
            try{
                StringBuilder sb = new StringBuilder();
                BufferedWriter bw = IOUtils.getTextWriter(new File (outputFileS).getAbsolutePath());
                sb.append("Gene"+"\t");
                for(int j=0;j<fileList[i].size();j++){
                    sb.append(fileList[i].get(j).getName().replaceAll("_UMICount.txt", "")+"\t");
                }
                bw.write(sb.toString().replaceAll("\\s+$", ""));
                bw.newLine();
                for(int k=0;k<count.length;k++){
                    sb = new StringBuilder();
                    for(int j=0;j<fileList[i].size();j++){
                        if(j==0){
                            sb.append(nameList.get(k)+"\t");
                        }
                        sb.append(count[k][j]+"\t");
                    }
                    bw.write(sb.toString().replaceAll("\\s+$", ""));
                    bw.newLine();
                }

                bw.flush();
                bw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void countTable(){
        List <String> plateList=new ArrayList<>();
        List <File> [] fileList=new List[fqFileSListR1.size()];//
        for(int i=0;i<fqFileSListR1.size();i++){//
            fileList[i]=new ArrayList<File>();
        }
//        String subCountDirS = new File ("/Users/xujun/Desktop/eQTL/total/geneCount").getAbsolutePath();
        String subCountDirS = new File (this.outputDirS,subDirS[2]).getAbsolutePath();
        File[] fs = new File(subCountDirS).listFiles();
        fs = IOUtils.listFilesEndsWith(fs, "RawCount.txt");
        List<File> fList = Arrays.asList(fs);
        int length=0;
        for(int i=0;i < fList.size();i++){
            length=fList.get(i).getName().split("_").length-2;
            String plate=fList.get(i).getName().split("_")[length];
            if(!plateList.contains(plate)){
                plateList.add(plate);
            }
            fileList[plateList.indexOf(plate)].add(fList.get(i));
        }
        int geneNumber=0;String geneName=null;
        Set<String> geneSet = new HashSet<String>();
        for(int i=0;i<fileList.length;i++){
            Collections.sort(fileList[i]);
        }
        int geneNmuber=0;
        StringBuilder wc = new StringBuilder();
        wc.append("wc -l ").append(fileList[0].get(0));
        String command = wc.toString();
        System.out.println(command);
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                geneNumber=Integer.valueOf(temp.split(" ")[0])-5;
            }
            p.waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<fileList.length;i++){
            Set<String> nameSet = new HashSet<String>();
            List <String> nameList=new ArrayList<>();
            int [][] count = new int [geneNumber][fileList[i].size()];
            List<File> list = fileList[i];
            list.stream().forEach(f -> {
                String temp=null;String[] tem = null;
                try{
                    BufferedReader br = IOUtils.getTextReader(f.getAbsolutePath());
                    int rowN=0;
                    while((temp = br.readLine()) != null){
                        List<String> tList= PStringUtils.fastSplit(temp);
                        tem = tList.toArray(new String[tList.size()]);
                        if(!tem[0].startsWith("__")){
                            if(!nameSet.contains(tem[0])){
                                nameList.add(tem[0]);
                            }
                            nameSet.add(tem[0]);
//                          int index1=nameList.indexOf(tem[0]);
                            count[rowN][list.indexOf(f)]=Integer.parseInt(tem[1]);
                            rowN++;
                        }
                    }

                }
                catch (Exception ex) {
                    System.out.println(tem[0]+"\t"+geneSet.size()+"\t1234");
                    ex.printStackTrace();

                }
            });
            File subDir = new File (this.outputDirS,subDirS[3]);
            String outputFileS = new File (subDir,fileList[i].get(0).getName().split("_")[length]+"_raw_countResult.txt").getAbsolutePath();
            try{
                StringBuilder sb = new StringBuilder();
                BufferedWriter bw = IOUtils.getTextWriter(new File (outputFileS).getAbsolutePath());
                sb.append("Gene"+"\t");
                for(int j=0;j<fileList[i].size();j++){
                    sb.append(fileList[i].get(j).getName().replaceAll("_RawCount.txt", "")+"\t");
                }
                bw.write(sb.toString().replaceAll("\\s+$", ""));
                bw.newLine();
                for(int k=0;k<count.length;k++){
                    sb = new StringBuilder();
                    for(int j=0;j<fileList[i].size();j++){
                        if(j==0){
                            sb.append(nameList.get(k)+"\t");
                        }
                        sb.append(count[k][j]+"\t");
                    }
                    bw.write(sb.toString().replaceAll("\\s+$", ""));
                    bw.newLine();
                }

                bw.flush();
                bw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void mkPosGeneMap () {
        String geneNameS=null;int gfIndex=0;
        try{
            BufferedReader br = IOUtils.getTextReader(geneAnnotationFileS);
            String temp = null;
            Set<String> geneSet = new HashSet<String>();
            Set<Integer> chrSet = new HashSet<>();
            String[] tem = null;
            String geneName = null;
            HashMap<String, Integer> geneChrMap = new HashMap();
            HashMap<String, Integer> geneMinMap = new HashMap();
            HashMap<String, Integer> geneMaxMap = new HashMap();
            HashMap<String, Byte> geneStrandMap = new HashMap();
            while ((temp = br.readLine()) != null) {
                List<String> tList= PStringUtils.fastSplit(temp);
                tem = tList.toArray(new String[tList.size()]);
                if (!tem[2].startsWith("exon")) continue;
                String[] te = tem[8].split(";");
                geneName=te[1].split(":")[1].substring(0,te[1].split(":")[1].length()-1);
                if (!geneSet.contains(geneName)) {
                    geneMinMap.put(geneName, Integer.MAX_VALUE);
                    geneMaxMap.put(geneName, Integer.MIN_VALUE);
                }
                geneSet.add(geneName);
                int min = Integer.parseInt(tem[3]);
                int max = Integer.parseInt(tem[4]);
                if (geneMinMap.get(geneName) > min) geneMinMap.put(geneName, min);
                if (geneMaxMap.get(geneName) < max) geneMaxMap.put(geneName, max);
                int chr = Integer.parseInt(tem[0]);
                chrSet.add(chr);
                geneChrMap.put(geneName, chr);
                if (tem[6].startsWith("-")) geneStrandMap.put(geneName, (byte)1);
                else geneStrandMap.put(geneName, (byte)0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            System.out.println(geneNameS+"\t"+gfIndex);
        }
    }

    private void parseParameters(String infileS) {
        List<String> pLineList = new ArrayList<>();
        try {
            BufferedReader br = IOUtils.getTextReader(infileS);
            String temp = null;
            boolean ifOut = false;
            if (!(temp = br.readLine()).equals("SiPAS-Profiler")) ifOut = true;
            if (!(temp = br.readLine()).equals("Author: Jun Xu, Fei Lu")) ifOut = true;
            if (!(temp = br.readLine()).equals("Email: junxu1048@genetics.ac.cn; flu@genetics.ac.cn")) ifOut = true;
            if (!(temp = br.readLine()).equals("Homepage: http://plantgeneticslab.weebly.com/")) ifOut = true;
            if (ifOut) {
                System.out.println("Thanks for using sampleParse.");
                System.out.println("Please keep the authorship in the parameter file. Program stops.");
                System.exit(0);
            }
            while ((temp = br.readLine()) != null) {
                if (temp.startsWith("#")) continue;
                if (temp.isEmpty()) continue;
                pLineList.add(temp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.alignType=pLineList.get(0);
        if(!pLineList.get(1).equals("")){
            this.multiMapN=Integer.parseInt(pLineList.get(1));
        }else{
            this.multiMapN=2;
        }
        if(!pLineList.get(2).equals("")){
            this.mismatchRate=Float.parseFloat(pLineList.get(2));
        }else{
            this.mismatchRate=(float)0.1;
        }
        if(!pLineList.get(3).equals("")){
            this.minNMatch=Integer.parseInt(pLineList.get(3));
        }else{
            this.minNMatch=80;
        }
        this.sampleInformationFileS = pLineList.get(4);
        this.outputDirS = pLineList.get(5);
        this.geneAnnotationFileS=pLineList.get(6);
        this.referenceGenomeFileS=pLineList.get(7);
        this.starPath=pLineList.get(8);
        for (int i = 0; i < this.subDirS.length; i++) {
            new File(this.outputDirS, subDirS[i]).mkdir();
        }
    }
    private void processTaxaAndBarcode () {
        RowTable<String> t = new RowTable<>(this.sampleInformationFileS);
        Set<String> fqSet = new HashSet<>();
        for (int i = 0; i < t.getRowNumber(); i++) {
            fqSet.add(t.getCell(i, 3));
            fqR1R2Map.putIfAbsent(t.getCell(i, 3), t.getCell(i, 4));
        }
        fqFileSListR1 = new ArrayList<>(fqSet);
        Collections.sort(fqFileSListR1);
        barcodeLengths = new TIntArrayList[fqFileSListR1.size()]; //不同的样本可能带有不同长度的barcode
        barcodeLists = new ArrayList[fqFileSListR1.size()];
        taxaLists = new ArrayList[fqFileSListR1.size()];
        barcodeTaxaMaps = new HashMap[fqFileSListR1.size()];
        int[] cnts = new int[fqFileSListR1.size()];
        for (int i = 0; i < t.getRowNumber(); i++) {
            int index = Collections.binarySearch(fqFileSListR1, t.getCell(i, 3));
            cnts[index]++;
        }
        for (int i = 0; i < cnts.length; i++) {
            barcodeLists[i] = new ArrayList<>(cnts[i]);
            taxaLists[i] = new ArrayList<>(cnts[i]);
            barcodeTaxaMaps[i] = new HashMap<>(cnts[i]);
            barcodeLengths[i] = new TIntArrayList(cnts[i]);
        }
        for (int i = 0; i < t.getRowNumber(); i++) {
            int index = Collections.binarySearch(fqFileSListR1, t.getCell(i, 3));
            String taxon = t.getCell(i, 0) + "_"+ t.getCell(i, 1);//这个连接起来的taxon就是我们要找的index信息
            allTaxaList.add(taxon);
            taxaLists[index].add(taxon);
            barcodeLists[index].add(t.getCell(i, 2));
            barcodeTaxaMaps[index].put(t.getCell(i, 2), taxon);
            barcodeLengths[index].add(t.getCell(i, 2).length());
        }
        Collections.sort(allTaxaList);
        StringBuilder sb = new StringBuilder();
        sb.append("There are ").append(fqSet.size()).append(" plates, or ").append(allTaxaList.size()).append(" taxa to be processed.");
        System.out.println(sb.toString());
    }

    public static void main(String args[]) {
        new SiPASUMIProfile (args[0]);
//        new sampleParse(args[0]);
    }
}

