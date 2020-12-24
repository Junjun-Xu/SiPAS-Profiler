package QTL;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static QTL.FindPos.readRange;

public class Simulation {
    public static void main(String[] args) throws IOException {
        Fasta ft = new Fasta();
        ft.setBlocks("IWGSC_v1.1_HC_20170706_transcripts.fasta");
        ArrayList<ArrayList<String>> tt = ft.getBlock();
        System.out.println(tt.get(2));
        ft.filter();
        ArrayList<Integer> startPos = Fasta.getRandomStartPos(ft.getFilterBlock());
        ArrayList<String> random350 = Fasta.randomSequence(ft.getFilterBlock(), 0);

        int[] slice = Fasta.randomCommon(0,ft.getFilterBlock().size(),10000);
        ArrayList<String> resName = new ArrayList<>();
        ArrayList<String> resSequence = new ArrayList<>();
        System.out.println(Arrays.toString(slice));

        for (int num:slice) {
            resName.add(ft.getFilterGeneName().get(num));
            String temp = Fasta.random(random350.get(num));
            resSequence.add(temp);
        }

        ArrayList<String> resForwardSequence = new ArrayList<>();
        ArrayList<String> resBackSequence = new ArrayList<>();
        for (String seq : resSequence) {
            String forward = (String) seq.subSequence(0, 150);
            resForwardSequence.add(forward);
            StringBuilder sb = new StringBuilder();
            for (int i =  seq.length()-1; i > (seq.length()-151); i--) {
                sb.append(seq.charAt(i));
            }
            resBackSequence.add(sb.toString());
        }


        ArrayList<ArrayList<Integer>> geneRange = new ArrayList<>();
        for (ArrayList<String> block: ft.getFilterBlock()) {
            ArrayList<Integer> res2 = readRange(block.get(0));
            geneRange.add(res2);
        }
        BufferedWriter br3 = new BufferedWriter(new FileWriter("information.txt"));
        br3.write("slice" + "\t" + "startPhyPos" + "\t" + "endPhyPos" + "\t" + "Chro");
        br3.newLine();
        int ii= 0;
        for (int num:slice) {
            br3.write(num + "\t" + geneRange.get(num).get(startPos.get(num)) + "\t" + geneRange.get(num).get(startPos.get(num) + 350) + "\t" + resName.get(ii).split(" ")[0]);
            br3.newLine();
            ii++;
        }
        br3.flush();
        br3.close();

        BufferedWriter br = new BufferedWriter(new FileWriter("front.txt"));
        for (int i = 0; i < resForwardSequence.size(); i++) {
            br.write(resForwardSequence.get(i));
            br.newLine();

        }
        br.flush();
        br.close();

        BufferedWriter br1 = new BufferedWriter(new FileWriter("back.txt"));
        for (int i = 0; i < resBackSequence.size(); i++) {
            br1.write(resBackSequence.get(i));
            br1.newLine();
        }
        br1.flush();
        br1.close();

    }
}
