/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import format.table.RowTable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import utils.Benchmark;
import utils.IOUtils;


/**
 *
 * @author xujun
 */
public class testInfor {
    public testInfor(String[] inputInf) {
        this.sampleInfo(inputInf);
        this.libraryInfo(inputInf[1]);
    }
    private void sampleInfo (String[] inputInf){
        long startTimePoint = System.nanoTime();
        RowTable rt = new RowTable(inputInf[0]);
        HashMap barcodeName = new HashMap();
        List<String> nameList = new ArrayList();
        for (int i=0;i<rt.getRowNumber();i++){
            barcodeName.put(rt.getCellAsString(i, 2), rt.getCellAsString(i, 1));
            nameList.add(rt.getCellAsString(i, 1));
        }
        File file = new File(inputInf[1]+"/Rawdata");
        String[] directories = file.list(new FilenameFilter() {
          public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        Arrays.sort(directories);
        for(int i=0;i<directories.length;i++){
            String inputDirS=inputInf[1]+"/"+directories[i];
            File[] fs = new File(inputInf[1]+"/"+directories[i]).listFiles();
            fs = IOUtils.listFilesEndsWith(fs, ".gz");
            HashSet<String> nameSet = new HashSet();
            for (int j = 0; j < fs.length; j++) {
                if (fs[j].isHidden()) continue;
                nameSet.add(fs[j].getName().split("_")[0]);
            }
            List<File> fList = Arrays.asList(fs);
            nameSet.stream().forEach(p -> {
                try {
                    String infile = new File (inputDirS, p+"_R1.fq.gz").getAbsolutePath();
                    BufferedWriter [] bw = new BufferedWriter[nameList.size()];
                    BufferedWriter be = IOUtils.getTextWriter(new File(inputDirS, "error_R1.fq").getAbsolutePath());
                    for (int k = 0; k < nameList.size(); k++) {
                       bw[k]=IOUtils.getTextWriter(new File(inputDirS, nameList.get(k)+"_R1.fq").getAbsolutePath());
                    }
                    BufferedReader br = utils.IOUtils.getTextGzipReader(infile);
                    String temp = null;String seq=null;
                    String currentBarcode=null;
                    while ((temp = br.readLine()) != null) {
                        seq=br.readLine();
                        if(barcodeName.get(seq.substring(0, 8))!=null){
                            int index = nameList.indexOf(barcodeName.get(seq.substring(0, 8)));
                            bw[index].write(temp);bw[index].newLine();
                            bw[index].write(seq);bw[index].newLine();
                            bw[index].write(br.readLine());bw[index].newLine();
                            bw[index].write(br.readLine());bw[index].newLine();
                        }else{
                            be.write(temp);be.newLine();
                            be.write(seq);be.newLine();
                            be.write(br.readLine());be.write(br.readLine());
                        }
                    }
                    br.close();
                    for(int k=0;k<bw.length;k++){
                        bw[k].flush();bw[k].close();
                    }
                    be.flush();be.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            //统计每个样本的reads数目和文件的大小
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("wc -l ").append(inputDirS).append("/*R1.fq");
                sb.append(" > "+ new File(inputInf[1]+"/"+directories[i]+"_RN"+".txt"));
                String command = sb.toString();
                System.out.println(command);
                File dir = new File(new File (inputInf[1]).getAbsolutePath());
                String [] cmdarry ={"/bin/bash","-c",command};
                Process p=Runtime.getRuntime().exec(cmdarry,null,dir);
                p.waitFor();p.destroy();
                
                StringBuilder sb1 = new StringBuilder();
                sb1.append("du -m ").append(inputDirS).append("/*R1.fq");
                sb1.append(" > "+ new File(inputInf[1]+"/"+directories[i]+"_FS"+".txt"));
                String command1 = sb1.toString();
                System.out.println(command1);
                String [] cmdarry1 ={"/bin/bash","-c",command1};
                Process p1=Runtime.getRuntime().exec(cmdarry1,null,dir);
                p1.waitFor();p1.destroy();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(Arrays.toString(directories));
        StringBuilder time = new StringBuilder();
        time.append("Distinguish samples according to barcode and trim the barcode.").append("Took ").append(Benchmark.getTimeSpanSeconds(startTimePoint)).append(" seconds. Memory used: ").append(Benchmark.getUsedMemoryGb()).append(" Gb");
        System.out.println(time.toString());
        
        //ratioTable file
        File[] fs = new File(inputInf[1]).listFiles();   
        fs = IOUtils.listFilesEndsWith(fs, "_RN.txt");
        List<File> fList = Arrays.asList(fs);
        Collections.sort(fList);
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
	defaultFormat.setMinimumFractionDigits(2);
        try{
            BufferedWriter bw = IOUtils.getTextWriter(new File(inputInf[1]+"/ratioTable.txt").getAbsolutePath());
            fList.stream().forEach(f -> {
                String temp=null;String tem = null;String plate=null;int total=0;int rn=0;
                try{           
                    BufferedReader br = IOUtils.getTextReader(f.getAbsolutePath());
                    plate=f.getName().replace("_RN.txt", "");
                    bw.write(plate);bw.write("\t");
                    bw.write("Barcode");bw.write("\t");
                    bw.write("Reads number");bw.write("\t");
                    bw.write("Ratio");bw.newLine();
                    while((temp = br.readLine()) != null){
                        if(temp.contains("total")){
                            total=Integer.valueOf(temp.split("total")[0].replaceAll(" ", ""));
                        }
                    }
                    BufferedReader br1 = IOUtils.getTextReader(f.getAbsolutePath());int count=-1;
                    while((temp = br1.readLine()) != null){
                        if(!temp.contains("total")){
                            count++;
                            tem=temp.split(plate+"/")[1].split("_R1.fq")[0];
                            if(temp.contains("error")){
                                bw.write(tem+"\t");bw.write(" "+"\t");
                            }else{
                                bw.write(tem+"\t");bw.write(getKey(barcodeName,tem)+"\t");
                            }
                            rn=Integer.valueOf(temp.split("/")[0].replaceAll(" ", ""));
                            bw.write(rn+"\t");
                            bw.write(defaultFormat.format((double)rn/total));bw.newLine();
                        }else{
                            bw.write("total"+"\t");bw.write(" "+"\t"); 
                            bw.write(total+"\t");bw.newLine();
                        }     
                    } 
                    br.close();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            bw.flush();bw.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        //baseTable file
        File[] fsb = new File(inputInf[1]).listFiles();   
        fsb = IOUtils.listFilesEndsWith(fsb, "_FS.txt");
        List<File> fListb = Arrays.asList(fsb);
        Collections.sort(fListb);
        try{
            BufferedWriter bw = IOUtils.getTextWriter(new File(inputInf[1]+"/baseTable.txt").getAbsolutePath());
            BufferedReader brr = IOUtils.getTextReader(new File(inputInf[1]+"/ratioTable.txt").getAbsolutePath());
            fListb.stream().forEach(f -> {
                String temp=null;String tem = null;String plate=null;int total=0;int rn=0;
                try{           
                    BufferedReader br = IOUtils.getTextReader(f.getAbsolutePath());
                    plate=f.getName().replace("_FS.txt", "");
                    bw.write(plate);bw.write("\t");
                    bw.write("Reads number");bw.write("\t");
                    bw.write("Base");bw.write("\t");
                    bw.write("Mbase");bw.newLine();
                    brr.readLine();
                    while((temp = br.readLine()) != null){
                        if(!temp.contains("error")){
                           tem=temp.split(plate+"/")[1].split("_R1.fq")[0];
                            bw.write(tem+"\t");
                            rn=Integer.valueOf(brr.readLine().split("\t")[2]);
                            bw.write(rn+"\t");
                            bw.write(rn*150*2+"\t");
                            bw.write(temp.split("\t")[0].replaceAll(" ", ""));bw.newLine(); 
                        } 
                    } 
                    brr.readLine();brr.readLine();
                    br.close();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            bw.flush();bw.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        //delet the redundant file 包括各个样本的fq文件、每个文库的统计输出（RN：reads number FS：file size）
        for(int i=0;i<directories.length;i++){
            File folderfq = new File(file.getAbsolutePath()+"/"+directories[i]);
            File fListfq[] = folderfq.listFiles();
            for(int j=0;j< fListfq.length;j++){
                if(fListfq[j].getName().endsWith("_R1.fq") ){
                    fListfq[j].delete();
                }
            }
        }
        File folderS=file;
        File fListS[]=folderS.listFiles();
        for(int j=0;j< fListS.length;j++){
            if(fListS[j].getName().endsWith("_RN.txt") || fListS[j].getName().endsWith("_FS.txt") ){
               fListS[j].delete();
            }
        }
       
        
    }
    private void libraryInfo (String inputFileDirS){//这个输入文件的直接路径下面同时有clear和raw这两个文件
        File fileC = new File(inputFileDirS+"/Cleandata");
        String[] directoriesC = fileC.list(new FilenameFilter() {
          public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        Arrays.sort(directoriesC);
        try{
            for(int i=0;i<directoriesC.length;i++){
                StringBuilder sb = new StringBuilder();
                sb.append("wc -l ").append(fileC+"/"+directoriesC[i]).append("/*R1.fq.gz");
                sb.append(" > "+ new File(inputFileDirS+"/"+directoriesC[i]+"_CleanSize.txt"));
                String command = sb.toString();
                System.out.println(command);
                File dir = new File(fileC.getAbsolutePath());
                String [] cmdarry ={"/bin/bash","-c",command};
                Process p=Runtime.getRuntime().exec(cmdarry,null,dir);
                p.waitFor();p.destroy();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        } 
        
        File fileR = new File(inputFileDirS+"/Rawdata");
        String[] directoriesR = fileR.list(new FilenameFilter() {
          public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        Arrays.sort(directoriesR);
        try{
            for(int i=0;i<directoriesR.length;i++){
                StringBuilder sb = new StringBuilder();
                sb.append("wc -l ").append(fileR+"/"+directoriesR[i]).append("/*R1.fq.gz");
                sb.append(" > "+ new File(inputFileDirS+"/"+directoriesR[i]+"_RawSize.txt"));
                String command = sb.toString();
                System.out.println(command);
                File dir = new File(fileC.getAbsolutePath());
                String [] cmdarry ={"/bin/bash","-c",command};
                Process p=Runtime.getRuntime().exec(cmdarry,null,dir);
                p.waitFor();p.destroy();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        List<String> nameList = new ArrayList();
        for (int i = 0; i < directoriesC.length; i++) {
            nameList.add(directoriesC[i]);
        }
        Collections.sort(nameList);
        try{
            BufferedWriter bw = IOUtils.getTextWriter(new File(inputFileDirS, "libraryOutput.txt").getAbsolutePath());
            bw.write("library"+"\t"+"clean data(Gbase)"+"\t"+"raw data(Gbase)");bw.newLine();
            nameList.stream().forEach(p -> {
                try {
                    String infile1 = new File (inputFileDirS, p+"_CleanSize.txt").getAbsolutePath();
                    String infile2 = new File (inputFileDirS, p+"_RawSize.txt").getAbsolutePath();
                    BufferedReader br = utils.IOUtils.getTextReader(infile1);
                    BufferedReader br1 = utils.IOUtils.getTextReader(infile2);
                    String temp = null;String seq=null;
                    String currentBarcode=null;
                    while ((temp = br.readLine()) != null) {
                        bw.write(p+"\t");
                        bw.write(Integer.valueOf(temp.split("/")[0].replaceAll(" ", ""))*150*2+"\t");
                        bw.write(Integer.valueOf(br1.readLine().split("/")[0].replaceAll(" ", ""))*150*2+"\t");bw.newLine();
                    }
                    br.close();br1.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            bw.flush();bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
       
        //delete the existing file
        for(int i=0;i<directoriesC.length;i++){
            File folder = new File(inputFileDirS);
            File fList[] = folder.listFiles();
            for(int j=0;j< fList.length;j++){
                if(fList[j].getName().endsWith("_CleanSize.txt") || fList[j].getName().endsWith("_RawSize.txt")){
                    fList[j].delete();
                }
            }
        }
       
    }
    public static <K, V> K getKey(Map<K, V> map, V value) {//通过value得到key
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
		return entry.getKey();
            }
	}
	return null;
    }
    
}
