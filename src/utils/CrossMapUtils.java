/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Usage:
 * 1. convert an input BED file
 * CrossMapUtils cm = new CrossMapUtils(f.getAbsolutePath(), outfileS);
 * cm.setMaizeV3ToV4();
 * cm.convert()
 * 2. get coordinates
 * CrossMapUtils cm = new CrossMapUtils(chr, pos, inputBedFileS);
 * cm.setMaizeV3ToV4();
 * cm.convert();
 * List<int[]> l  = cm.getConvertedCoordinate();
 * cm.deleteBedFiles();
 * @author feilu
 */
public class CrossMapUtils {
    public static String myPythonPath = "/Library/Frameworks/Python.framework/Versions/2.7/bin/python";
    public static String myCrossMapPath = "/Library/Frameworks/Python.framework/Versions/2.7/bin/CrossMap.py";
    public static String myMaizeChainPath = "/Users/feilu/Documents/database/maize/crossMap/AGPv3_to_AGPv4.chain.gz";
    String crossMapPath = null;
    String pythonPath = null;
    String inputBedFileS = null;
    String outputBedFileS = null;
    String chainFilePath = null;
    
    public CrossMapUtils (String pythonPath, String crossMapPath, String chainFilePath, String inputBedFileS, String outputBedFileS) {
        this.crossMapPath = crossMapPath;
        this.chainFilePath = chainFilePath;
        this.pythonPath = pythonPath;
        this.inputBedFileS = inputBedFileS;
        this.outputBedFileS = outputBedFileS;
    }
    
    
    public CrossMapUtils (String inputBedFileS, String outputBedFileS) {
        this.inputBedFileS = inputBedFileS;
        this.outputBedFileS = outputBedFileS;
    }
    
    public CrossMapUtils (int[] chr, int[] pos, String inputBedFileS) {
        this.writeInputBed(chr, pos, inputBedFileS);
    }
    
    private void writeInputBed (int[] chr, int[] pos, String inputBedFileS) {
        try {
            BufferedWriter bw = IOUtils.getTextWriter(inputBedFileS);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chr.length; i++) {
                sb = new StringBuilder();
                sb.append(chr[i]).append("\t").append(pos[i]-1).append("\t").append(pos[i]-1);
                bw.write(sb.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.inputBedFileS = inputBedFileS;
        this.outputBedFileS = inputBedFileS+".con";
    }
    
    /**
     * Return -1 if converted position is not available or on Contigs
     * @return 
     */
    public List<int[]> getConvertedCoordinate () {
        TIntArrayList chrList = new TIntArrayList();
        TIntArrayList posList = new TIntArrayList();
        String unmapFileS = outputBedFileS+".unmap";
        String mapFileS = outputBedFileS;
        TIntArrayList unPosList = new TIntArrayList();
        try {
            BufferedReader br = IOUtils.getTextReader(unmapFileS);
            String temp = null;
            List<String> l = null;
            while ((temp = br.readLine()) != null) {
                l = PStringUtils.fastSplit(temp);
                unPosList.add(Integer.parseInt(l.get(1)));
            }
            br.close();
            int[] unPos = unPosList.toArray();
            Arrays.sort(unPos);
            br = IOUtils.getTextReader(inputBedFileS);
            BufferedReader br2 = IOUtils.getTextReader(mapFileS);
            String temp1 = null;
            
            int cnt = 0;
            while ((temp = br.readLine()) != null) {
                l = PStringUtils.fastSplit(temp);
                int query = Integer.parseInt(l.get(1));
                int index = Arrays.binarySearch(unPos, query);
                int pos = query+1;
                if (index < 0)  {
                    cnt++;
                    temp1 = br2.readLine();
                    l = PStringUtils.fastSplit(temp1);
                    if (Character.isDigit(l.get(0).charAt(0))) {
                        chrList.add(Integer.parseInt(l.get(0)));
                        posList.add(Integer.parseInt(l.get(1))+1);
                    }
                    else {
                        chrList.add(-1);
                        posList.add(-1);
                    }
                }
                else {
                    chrList.add(-1);
                    posList.add(-1);
                    cnt++;
                }

            }
            br.close();
            br2.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        List<int[]> l = new ArrayList<>();
        l.add(chrList.toArray());
        l.add(posList.toArray());
        return l;
    }
    
    public void deleteBedFiles () {
        new File (this.inputBedFileS).delete();
        new File (this.outputBedFileS).delete();
        String unmapFileS = outputBedFileS+".unmap";
        new File (unmapFileS).delete();
    }
    
    public void setMaizeV3ToV4 () {
        this.crossMapPath = myCrossMapPath;
        this.chainFilePath = myMaizeChainPath;
        this.pythonPath = myPythonPath;
    }
    
    public void convert () {
        StringBuilder sb = new StringBuilder(this.pythonPath);
        sb.append(" ").append(this.crossMapPath).append(" bed ").append(this.chainFilePath).append(" ").append(this.inputBedFileS).append(" ").append(this.outputBedFileS);
        try{
            String command = sb.toString();
            System.out.println(command);
            Runtime run = Runtime.getRuntime();
            Process p = run.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                System.out.println("temp");
                
            }
            p.waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
