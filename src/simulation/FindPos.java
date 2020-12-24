package QTL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FindPos {

//    public static void main(String[] args) throws IOException {
//        ArrayList<String> res1 = readFasta("testfxj.txt");
//        ArrayList<Integer> res2 = readRange(res1.get(0));
//        System.out.println(res2);
//    }

    public static ArrayList<String> readFasta(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        ArrayList<String> res = new ArrayList<>();
        String temp;
        res.add(br.readLine());
        StringBuilder sb = new StringBuilder();
        while ((temp = br.readLine()) != null){
            sb.append(temp);
        }
        res.add(sb.toString());
        return res;
    }

    public static ArrayList<String> readFasta(ArrayList<String> block) {
//        BufferedReader br = new BufferedReader(new FileReader(filename));
        ArrayList<String> res = new ArrayList<>();
        res.add(block.get(0));
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < block.size(); i++) {
            sb.append(block.get(i));
        }
        res.add(sb.toString());
        return res;
    }

    public static ArrayList<int[]> readGap(String file){
        String temp = file.split(" ")[6];
        String temp2 =  temp.split(":")[1];
        String[] temp3 = temp2.split(",");
        ArrayList<int[]> res = new ArrayList<>();
        for (String ss:temp3) {
            String[] temp4 = ss.split("-");
            int[] temp5 = new int[2];
            for (int i = 0; i < temp5.length; i++) {
                temp5[i] = Integer.parseInt(temp4[i]);
            }
            res.add(temp5);
        }
        return res;
    }


    public static int[] qwe(int[] aa) {
        int a = aa[0];
        int b = aa[1];
        int[] xx=new int[b-a +1];
        for (int i = 0; i <xx.length; i++) {
            xx[i] = a;
            a++;
        }
        return xx;
    }

    public static ArrayList<Integer> combine(int[] aa, int[] bb){
        ArrayList<Integer> res = new ArrayList<>();
        for (int num:aa) {
            res.add(num);
        }
        for (int num:bb) {
            res.add(num);
        }
        return res;
    }

    public static ArrayList<Integer> combine(ArrayList<Integer> aa, int[] bb){
        for (int num:bb) {
            aa.add(num);
        }
        return aa;
    }


    public static ArrayList<Integer> readRange(String file){
        ArrayList<int[]> temp = readGap(file);
        ArrayList<int[]> tt = new ArrayList<>();
        for (int[] xx : temp) {
            tt.add(qwe(xx));
        }
        ArrayList<Integer> res = new ArrayList<>();
        if (tt.size() ==1){
            res = combine(tt.get(0), new int[]{});
        } else if (tt.size() ==2){
            res = combine(tt.get(0), tt.get(1));
        }else {
            res = combine(tt.get(0), tt.get(1));
            for (int i = 2; i < tt.size(); i++) {
                res = combine(res, tt.get(i));
            }
        }
        return res;
    }


}
