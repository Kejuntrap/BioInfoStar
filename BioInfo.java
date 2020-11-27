import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class BioInfo extends JFrame implements KeyListener {
    static final int[][] BLOSUM62 =
            {
                    {4, -1, -2, -2, 0, -1, -1, 0, -2, -1, -1, -1, -1, -2, -1, 1, 0, -3, -2, 0},
                    {-1, 5, 0, -2, -3, 1, 0, -2, 0, -3, -2, 2, -1, -3, -2, -1, -1, -3, -2, -3},
                    {-2, 0, 6, 1, -3, 0, 0, 0, 1, -3, -3, 0, -2, -3, -2, 1, 0, -4, -2, -3},
                    {-2, -2, 1, 6, -3, 0, 2, -1, -1, -3, -4, -1, -3, -3, -1, 0, -1, -4, -3, -3},
                    {0, -3, -3, -3, 9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1},
                    {-1, 1, 0, 0, -3, 5, 2, -2, 0, -3, -2, 1, 0, -3, -1, 0, -1, -2, -1, -2},
                    {-1, 0, 0, 2, -4, 2, 5, -2, 0, -3, -3, 1, -2, -3, -1, 0, -1, -3, -2, -2},
                    {0, -2, 0, -1, -3, -2, -2, 6, -2, -4, -4, -2, -3, -3, -2, 0, -2, -2, -3, -3},
                    {-2, 0, 1, -1, -3, 0, 0, -2, 8, -3, -3, -1, -2, -1, -2, -1, -2, -2, 2, -3},
                    {-1, -3, -3, -3, -1, -3, -3, -4, -3, 4, 2, -3, 1, 0, -3, -2, -1, -3, -1, 3},
                    {-1, -2, -3, -4, -1, -2, -3, -4, -3, 2, 4, -2, 2, 0, -3, -2, -1, -2, -1, 1},
                    {-1, 2, 0, -1, -3, 1, 1, -2, -1, -3, -2, 5, -1, -3, -1, 0, -1, -3, -2, -2},
                    {-1, -1, -2, -3, -1, 0, -2, -3, -2, 1, 2, -1, 5, 0, -2, -1, -1, -1, -1, 1},
                    {-2, -3, -3, -3, -2, -3, -3, -3, -1, 0, 0, -3, 0, 6, -4, -2, -2, 1, 3, -1},
                    {-1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4, 7, -1, -1, -4, -3, -2},
                    {1, -1, 1, 0, -1, 0, 0, 0, -1, -2, -2, 0, -1, -2, -1, 4, 1, -3, -2, -2},
                    {0, -1, 0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1, 1, 5, -2, -2, 0},
                    {-3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1, 1, -4, -3, -2, 11, 2, -3},
                    {-2, -2, -2, -3, -2, -1, -2, -3, 2, -1, -1, -2, -1, 3, -3, -2, -2, 2, 7, -1},
                    {0, -3, -3, -3, -1, -2, -2, -3, -3, 3, 1, -2, 1, -1, -2, -2, 0, -3, -1, 4}
            };
    //ブロッサム62のスコア行列
    static final int GAPSCORE = -12;     //ギャップのペナルティ
    static final int[] AMINOINDEX =
            {0, -1, 4, 3, 6, 13, 7, 8, 9, -1, 11, 10, 12, 2,
                    1, 14, 5, 1, 15, 16, -1, 19, 17, -1, 18, -1}; //ブロッサム62の配列番号と対応するアルファベットの順序との対応
    static final int NEGATIVE_INF = -1_000_000_000;     //負の無限大
    static String[] sequences;      //アライメントを求める文字列を入れる配列
    static final char GAPCHAR = '-';        //ギャップに相当する文字
    static String[] alignment;      //アライメントを求めた文字列を入れる配列
    static int[] alignmentIndex;    //ソートしたアライメントの順番を入れる配列
    static BioInfo b;       //GUIウィンドウ
    static final int defaultX = 135;        //1行に表示する文字数
    static final int defaultY = 30;             //1ページに表示するアライメントの行数
    static final int defaultFontSize = 20;      //フォントサイズ
    static int pointingXs = 0;      //横のページのカウント(0インデックス)
    static int pointingYs = 0;       //縦のページのカウント(0インデックス)
    static int maxAlignmentLength = 0;  //アライメントの長さ
    static int maxPointingX = 0;        //横ページ数の最大(0インデックス)
    static int maxPointingY = 0;        //縦ページ数の最大(0インデックス)
    static int zeropadding = 0;     //001のようにパディングする0の個数を管理

    //入力配列
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int sequenceVolume = sc.nextInt();
        zeropadding = String.valueOf(sequenceVolume).length();
        sequences = new String[sequenceVolume];
        for (int i = 0; i < sequenceVolume; i++) {
            sequences[i] = sc.next().toUpperCase();     //大文字に変換しておく
        }
        long start= System.currentTimeMillis();
        alignment = Star(sequences, sequenceVolume);    //アライメント計算
        b = new BioInfo();      //GUIを起動
        long end=System.currentTimeMillis();
        System.out.println(end-start+"ms");     //時間計測
    }

    BioInfo() { //GUIのセッティング
        DrawPanel drawPanel = new DrawPanel();
        setSize(1440, 800);
        add(drawPanel);
        addKeyListener(this);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        Font font = new Font("Monospaced", 0, defaultFontSize);
        g.setFont(font);
        Graphics graphics = getContentPane().getGraphics();
        int startAlignment = Math.min(pointingYs * defaultY, alignment.length - 1);
        int endAlignment = Math.min((pointingYs + 1) * defaultY, alignment.length);
        int startCharIndex = Math.min(pointingXs * defaultX, maxAlignmentLength - 1);
        int endCharIndex = Math.min((pointingXs + 1) * defaultX, maxAlignmentLength);
        g.drawString("Alignment: " + startAlignment + "-" + (endAlignment-1), 20, 60);
        g.drawString("Character Index: " + startCharIndex + "-" + (endCharIndex-1), 20, 80);

        Color sinsui = new Color(192, 255, 255);    //親水性アミノ酸の背景
        Color sosui = new Color(255, 224, 224);     //疎水性アミノ酸の背景
        int counter = 0;        //アライメント描写における位置調整
        for(int i=startAlignment; i<endAlignment; i++){
            int calcY = 165 + defaultFontSize * counter;
            g.drawString("No:"+String.format("%0"+zeropadding+"d", alignmentIndex[i]),5,calcY);
            counter++;
        }
        counter=0;
        for (int i = startAlignment; i < endAlignment; i++) {
            for (int j = startCharIndex; j < endCharIndex; j++) {
                int calcX = 85 + defaultFontSize / 2 * (j - Math.min(pointingXs * defaultX, maxAlignmentLength - 1));
                int calcY = 165 + defaultFontSize * (counter - 1);
                int w = defaultFontSize / 2;
                int h = defaultFontSize;
                char amino = alignment[i].charAt(j);       //今見ているアミノ酸
                if (amino == 'D' || amino == 'E') {     //酸性アミノ酸
                    g.setColor(sinsui);
                    g.fillRect(calcX, calcY, w, h);
                } else if (amino == 'M' || amino == 'C') {       //含硫アミノ酸
                    if (amino == 'M') {        //メチオニンは疎水性アミノ酸
                        g.setColor(sosui);
                        g.fillRect(calcX, calcY, w, h);
                    } else {
                        g.setColor(sinsui);
                        g.fillRect(calcX, calcY, w, h);
                    }
                } else if (amino == 'S' || amino == 'N' || amino == 'Q' || amino == 'T') {   //親水性アミノ酸 中性
                    g.setColor(sinsui);
                    g.fillRect(calcX, calcY, w, h);
                } else if (amino == 'K' || amino == 'R' || amino == 'H') {        //塩基性アミノ酸
                    g.setColor(sinsui);
                    g.fillRect(calcX, calcY, w, h);
                } else if (amino == 'Y' || amino == 'W' || amino == 'F') {     //芳香族アミノ酸
                    g.setColor(sosui);
                    g.fillRect(calcX, calcY, w, h);
                } else if (amino == GAPCHAR) {      //ギャップ
                } else {      //疎水性
                    g.setColor(sosui);
                    g.fillRect(calcX, calcY, w, h);
                }
            }
            counter++;
        }
        counter = 0;
        for (int i = startAlignment; i < endAlignment; i++) {
            for (int j = startCharIndex; j < endCharIndex; j++) {
                int calcX = 85 + defaultFontSize / 2 * (j - Math.min(pointingXs * defaultX, maxAlignmentLength - 1));
                int calcY = 165 + defaultFontSize * counter;
                char amino = alignment[i].charAt(j);
                if (amino == 'D' || amino == 'E') {     //酸性アミノ酸
                    g.setColor(new Color(215, 0, 0));
                } else if (amino == 'M' || amino == 'C') {       //含硫アミノ酸
                    g.setColor(new Color(255, 127, 0));
                } else if (amino == 'S' || amino == 'N' || amino == 'Q' || amino == 'T') {   //親水性アミノ酸 中性
                    g.setColor(Color.BLACK);
                } else if (amino == 'K' || amino == 'R' || amino == 'H') {        //塩基性アミノ酸
                    g.setColor(new Color(0, 0, 215));
                } else if (amino == 'Y' || amino == 'W' || amino == 'F') {     //芳香族アミノ酸
                    g.setColor(new Color(0, 128, 96));
                } else if (amino == GAPCHAR) {      //ギャップ
                    g.setColor(Color.BLACK);
                } else {      //疎水性
                    g.setColor(Color.BLACK);
                }
                g.drawString(String.valueOf(amino), calcX, calcY);
            }
            counter++;
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'W' || e.getKeyChar() == 'w') {
            if (pointingYs - 1 >= 0) {
                pointingYs--;
            }
        } else if (e.getKeyChar() == 'S' || e.getKeyChar() == 's') {
            if (pointingYs + 1 <= maxPointingY) {
                pointingYs++;
            }
        } else if (e.getKeyChar() == 'A' || e.getKeyChar() == 'a') {
            if (pointingXs - 1 >= 0) {
                pointingXs--;
            }
        } else if (e.getKeyChar() == 'D' || e.getKeyChar() == 'd') {
            if (pointingXs + 1 <= maxPointingX) {
                pointingXs++;
            }
        }
        b.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static class DrawPanel extends JPanel {
        Graphics g = getGraphics();

        DrawPanel() {
            setBackground(Color.WHITE);
        }

    }

    public static String[] Star(String[] sequences, int sequenceVolume) {
        //類似度が高い順にソートする作業と類似度を計算する
        ArrayList<Sequence> storePairsum = new ArrayList<Sequence>();
        for (int i = 0; i < sequenceVolume; i++) {
            storePairsum.add(new Sequence(0, i));
            for (int j = 0; j < sequenceVolume; j++) {
                if (i != j) {
                    int tmpsimScore = calcSimilarityFast(sequences[i], sequences[j]);          //類似度計算する関数
                    storePairsum.get(i).similarity += tmpsimScore;          //自分以外の配列との類似度を計算してその和が最大が中心アライメントになる
                }
            }
        }
        storePairsum.sort(Comparator.comparing(seq -> -seq.similarity));
        alignmentIndex = new int[sequenceVolume];     //類似度が高いものの順にソートしたのでアライメントの添字が変わっているので，それを記録する
        for (int i = 0; i < sequenceVolume; i++) {
            alignmentIndex[i] = storePairsum.get(i).seqindex;
        }
        //類似度が高い順にソートする作業と類似度を計算する おわり
        //中心にするアライメントがわかったので経路を保存したアライメントを求める
        String[] retAlignment = new String[sequenceVolume];    //値を返すアライメント入れる変数
        ArrayList<ArrayList<ArrayList<Integer>>> storeAlignment = new ArrayList<ArrayList<ArrayList<Integer>>>();       //最後のアライメント融合に適した様にデータを保存する
        for (int i = 0; i < sequenceVolume - 1; i++) {  //全部の配列から２つとってアライメントを計算するが，１つは固定なので合計で配列の数-1回アライメント計算が要る
            String[] tmpAlignment = calcSimilaritySlow(sequences[alignmentIndex[0]], sequences[alignmentIndex[i + 1]]);
            ArrayList<ArrayList<Integer>> fixedAlignment = calcIndex(tmpAlignment, sequences[alignmentIndex[0]].length());
            storeAlignment.add(fixedAlignment);
        }
        int[] maxDistance = new int[sequences[alignmentIndex[0]].length() + 1];       //ギャップを入れる
        Arrays.fill(maxDistance, 0);
        for (int i = 0; i < storeAlignment.size(); i++) {
            for (int j = 0; j < storeAlignment.get(i).size(); j++) {
                maxDistance[j] = Math.max(maxDistance[j], storeAlignment.get(i).get(j).size());
            }
        }
        //経路探索終了
        //中心アライメントの結果を文字列に落とし込む
        for (int h = -1; h < storeAlignment.size(); h++) {
            StringBuilder sb = new StringBuilder();
            if (h == -1) {      //センターのアライメント
                for (int i = 0; i < sequences[alignmentIndex[0]].length() + 1; i++) {
                    for (int j = 0; j < maxDistance[i]; j++) {
                        if (i == 0) {
                            sb.append(GAPCHAR);
                        } else {
                            if (j == 0) {
                                sb.append(sequences[alignmentIndex[0]].charAt(i - 1));
                            } else {
                                sb.append(GAPCHAR);
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < sequences[alignmentIndex[0]].length() + 1; i++) {
                    for (int j = 0; j < maxDistance[i]; j++) {
                        if (j < storeAlignment.get(h).get(i).size()) {
                            sb.append(String.valueOf((char) ((int) (storeAlignment.get(h).get(i).get(j)))));
                        } else {
                            sb.append(GAPCHAR);
                        }
                    }
                }
            }
            retAlignment[h + 1] = sb.toString();
        }
        //中心アライメントの結果を文字列に落とし込む おわり
        //アライメントに関する小計算
        for (int i = 0; i < retAlignment.length; i++) {
            maxAlignmentLength = Math.max(maxAlignmentLength, retAlignment[i].length());
        }
        maxPointingY = (retAlignment.length - 1) / defaultY;
        maxPointingX = (maxAlignmentLength - 1) / defaultX;
        //アライメントに関する小計算おわり
        return retAlignment;
    }

    public static ArrayList<ArrayList<Integer>> calcIndex(String[] s, int masterCharVol /*   最大文字数 */) {
        int[] masterIndex = new int[s[0].length()];
        Arrays.fill(masterIndex, -1);
        ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < masterCharVol + 1; i++) {       //+1は中心アライメント開始前のギャップをほぞんする
            ret.add(new ArrayList<Integer>());
        }
        int nowindex = -1;      //中心アライメントよりも前にギャップがあるのをマイナス１として扱う
        for (int i = 0; i < s[0].length(); i++) {
            if (s[0].charAt(i) == GAPCHAR) {
                //
            } else {
                nowindex++;
            }
            masterIndex[i] = nowindex;
            ret.get(masterIndex[i] + 1).add((int) s[1].charAt(i));
        }
        return ret;
    }

    public static int calcSimilarityFast(String s1, String s2) {  //経路保存しないで似ている度合いを確かめる
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < s1.length() + 1; i++) {
            Arrays.fill(dp[i], NEGATIVE_INF);       //一応初期化
        }
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i * GAPSCORE;
        }
        for (int i = 0; i <= s2.length(); i++) {
            dp[0][i] = i * GAPSCORE;
        }
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                dp[i][j] = Math.max(Math.max(dp[i - 1][j] + GAPSCORE, dp[i][j - 1] + GAPSCORE), dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1)));     //レーヴェンシュタイン距離と同じ
            }
        }
        return dp[s1.length()][s2.length()];
    }

    public static String[] calcSimilaritySlow(String s1, String s2) {  //経路保存して似ている度合いを確かめる
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        int[][] pathway = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < s1.length() + 1; i++) {
            Arrays.fill(dp[i], NEGATIVE_INF);       //一応初期化
            Arrays.fill(pathway[i], -1);     //経路　そこから始まる時-1
        }
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i * GAPSCORE;
        }
        for (int i = 0; i <= s2.length(); i++) {
            dp[0][i] = i * GAPSCORE;
        }
        /*
        -> j増加
        i 　| 0 | 1 |
        増加| 2 | 遷移
        遷移なし -1
         */
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (dp[i - 1][j] + GAPSCORE == dp[i][j - 1] + GAPSCORE && dp[i][j - 1] + GAPSCORE == dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1))) {      //遷移先の値が全て同じ値を取る時
                    if (GAPSCORE <= dif(s1.charAt(i - 1), s2.charAt(j - 1))) {       //できるだけ常にマッチ度を最大にするため，1つ前の状態が最大のものから引き継ぐとする
                        if (s1.length() > s2.length()) {      //短い方にギャップをつけることで長さの不均衡を抑えておく
                            dp[i][j] = dp[i - 1][j] + GAPSCORE;
                            pathway[i][j] = 1;      //iが増加する方のギャップを付け足すことによる遷移を表す1
                        } else if (s1.length() < s2.length()) {    //短い方にギャップをつけることで長さの不均衡を抑えておく
                            dp[i][j] = dp[i][j - 1] + GAPSCORE;
                            pathway[i][j] = 2;
                        } else if (s1.length() == s2.length()) {   //同じならどっちかにしておく
                            dp[i][j] = dp[i - 1][j] + GAPSCORE;
                            pathway[i][j] = 1;
                        }
                    } else {
                        dp[i][j] = dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1));
                        pathway[i][j] = 0;
                    }
                } else if (dp[i - 1][j] + GAPSCORE == dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1))) {      //遷移先の値のうち2つが同じ値を取る時 その1
                    if (dp[i - 1][j] + GAPSCORE < dp[i][j - 1] + GAPSCORE) {        //スコアが同じでない所のほうが結果が良い場合はそっちを採用する
                        dp[i][j] = dp[i][j - 1] + GAPSCORE;
                        pathway[i][j] = 2;
                    } else {
                        if (GAPSCORE < dif(s1.charAt(i - 1), s2.charAt(j - 1))) {      //できるだけ前の遷移前の値も最大化したい
                            dp[i][j] = dp[i - 1][j] + GAPSCORE;     //ミスマッチよりもギャップのほうがペナルティが小さい場合ギャップを選ぶ
                            pathway[i][j] = 1;
                        } else {
                            dp[i][j] = dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1));      //ミスマッチのほうがスコアが良い場合はミスマッチさせる
                            pathway[i][j] = 0;
                        }
                    }

                } else if (dp[i][j - 1] + GAPSCORE == dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1))) {      //遷移先の値のうち2つが同じ値を取る時 その2
                    if (dp[i][j - 1] + GAPSCORE < dp[i - 1][j] + GAPSCORE) {        //スコアが同じでない所のほうが結果が良い場合はそっちを採用する
                        dp[i][j] = dp[i - 1][j] + GAPSCORE;
                        pathway[i][j] = 1;
                    } else {
                        if (GAPSCORE < dif(s1.charAt(i - 1), s2.charAt(j - 1))) {      //できるだけ前の遷移前の値も最大化したい
                            dp[i][j] = dp[i][j - 1] + GAPSCORE;     //ミスマッチよりもギャップのほうがペナルティが小さい場合ギャップを選ぶ
                            pathway[i][j] = 2;
                        } else {
                            dp[i][j] = dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1));      //ミスマッチのほうがスコアが良い場合はミスマッチさせる
                            pathway[i][j] = 0;
                        }
                    }
                } else if (dp[i][j - 1] + GAPSCORE == dp[i - 1][j] + GAPSCORE) {      //遷移先の値のうち2つが同じ値を取る時 その3
                    if (dp[i][j - 1] + GAPSCORE < dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1))) {
                        dp[i][j] = dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1));
                        pathway[i][j] = 0;
                    } else {
                        if (s1.length() > s2.length()) {      //短い方にギャップをつけることで長さの不均衡を抑えておく
                            dp[i][j] = dp[i - 1][j] + GAPSCORE;
                            pathway[i][j] = 1;
                        } else if (s1.length() < s2.length()) {    //短い方にギャップをつけることで長さの不均衡を抑えておく
                            dp[i][j] = dp[i][j - 1] + GAPSCORE;
                            pathway[i][j] = 2;
                        } else if (s1.length() == s2.length()) {   //同じならどっちかにしておく
                            dp[i][j] = dp[i - 1][j] + GAPSCORE;
                            pathway[i][j] = 1;
                        }
                    }

                } else {
                    dp[i][j] = Math.max(Math.max(dp[i - 1][j] + GAPSCORE, dp[i][j - 1] + GAPSCORE), dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1)));     //レーヴェンシュタイン距離と同じ
                    int maxvalue = Math.max(Math.max(dp[i - 1][j] + GAPSCORE, dp[i][j - 1] + GAPSCORE), dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1)));
                    if (maxvalue == dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1))) {
                        dp[i][j] = dp[i - 1][j - 1] + dif(s1.charAt(i - 1), s2.charAt(j - 1));
                        pathway[i][j] = 0;
                    } else if (maxvalue == dp[i - 1][j] + GAPSCORE) {
                        dp[i][j] = dp[i - 1][j] + GAPSCORE;
                        pathway[i][j] = 1;
                    } else {
                        dp[i][j] = dp[i][j - 1] + GAPSCORE;
                        pathway[i][j] = 2;
                    }
                }
            }
        }
        StringBuilder[] sb = new StringBuilder[2];
        sb[0] = new StringBuilder();
        sb[1] = new StringBuilder();
        int iindex, jindex;
        iindex = s1.length();
        jindex = s2.length();
        while (true) {
            if (pathway[iindex][jindex] == -1) {
                break;
            } else if (pathway[iindex][jindex] == 0) {
                sb[0].append(s1.charAt(iindex - 1));
                sb[1].append(s2.charAt(jindex - 1));
                iindex--;
                jindex--;
            } else if (pathway[iindex][jindex] == 1) {
                sb[0].append(s1.charAt(iindex - 1));
                sb[1].append(GAPCHAR);
                iindex--;
            } else if (pathway[iindex][jindex] == 2) {
                sb[0].append(GAPCHAR);
                sb[1].append(s2.charAt(jindex - 1));
                jindex--;
            }
        }
        if (iindex >= 1) {
            for (; iindex >= 1; iindex--) {
                sb[0].append(s1.charAt(iindex - 1));
                sb[1].append(GAPCHAR);
            }
        }
        if (jindex >= 1) {
            for (; jindex >= 1; jindex--) {
                sb[0].append(GAPCHAR);
                sb[1].append(s2.charAt(jindex - 1));
            }
        }
        sb[0].reverse();
        sb[1].reverse();
        return new String[]{sb[0].toString(), sb[1].toString()};
    }

    public static int dif(char a, char b) {     //２つのアミノ酸の類似度を返す
        if (AMINOINDEX[a - 'A'] >= 0 && AMINOINDEX[b - 'A'] >= 0) {
            return BLOSUM62[AMINOINDEX[a - 'A']][AMINOINDEX[b - 'A']];      //適合があった場合
        }
        //アミノ酸1文字レターコードがなかった場合
        System.out.println("Unknown Amino Acid Symbol.");
        System.exit(-1);
        return NEGATIVE_INF;
    }

    public static class Sequence {  //類似度計算のための類似度を保存する関数
        int similarity;
        int seqindex;

        Sequence(int sim, int index) {
            similarity = sim;
            seqindex = index;
        }
    }
}

/*
サンプル

17
mkpiiavnykayypysfgenalriardakrvweetgvevilappfteiyrvlkevegsgvkvfaqhadpvepgavtgyipveglkeagvhgvilnhsehrlkiadinaliikarrlglktlacadvpetgaaiallkpdmiaveppeligtgvsvskakpevitnsvsmirsvnkealiltgagittgedvyqavklgtigvlvasgivkakdpysvmkdmalnalkavs
maprkffvggnwkmngdkkslgelihtlngaklsadtevvcgapsiyldfarqkldakigvaaqncykvpkgaftgeispamikdigaawvilghserrhvfgesdeligqkvahalaeglgviacigekldereagitekvvfeqtkaiadnvkdwskvvlayepvwaigtgktatpqqaqevheklrgwlkshvsdavaqstriiyggsvtggnckelasqhdvdgflvggaslkpefvdiinakh
maatsltappsfsglrrispkldaaavsshqsffhrvnsstrlvsssssshrsprgvvamagsgkngtkdsiaklisdlnsatleadvdvvvsppfvyidqvkssltdridisgqnswvgkggaftgeisveqlkdlgckwvilghserrhvigekdefigkkaayalseglgviacigekleereagktfdvcfaqlkafadavpswdnivvayepvwaigtgkvaspqqaqevhvavrgwlkknvseevasktriiyggsvnggnsaelakeedidgflvggaslkgpefativnsvtskkvaa
markffvggnwkcngtaeevkkivntlneaqvpsqdvvevvvsppyvflplvkstlrsdffvaaqncwvkkggaftgevsaemlvnldipwvilghserrailnessefvgdkvayalaqglkviacvgetleereagstmdvvaaqtkaiadrvtnwsnvviayepvwaigtgkvaspaqaqevhdelrkwlaknvsadvaattriiyggsvnggnckelggqadvdgflvggaslkpefidiikaaevkksa
mrqiiiagnwkmhktqtesleflqgflshledtpeeretvlcvpftclnfmsknlhgsrvklgaqnihwadqgaftgeisgemlkefginyvivghserrqyfgetdetvnarllaaqkhgltpilcvgeskaqrdageteavisaqiekdlvnvdqnnlviayepiwaigtgdtceaaeanrviglirsqltnknvtiqyggsvnpknvdeimaqpeidgalvggasldpesfarlvnyq
mhktqaesleflqsflpqlentaedrevilcapytalgvmsknlhgtrvrigsqnvhweesgaftgeiapsmlteigvtyavvghserrqyfgetdetvnfraraaqkaeltpilcvgeskeqrdagqtetvikeqlkadlvgvdlsqlviayepiwaigtgdtceaeeanrvigmirselsssdvpiqyggsvkpanideimaqpeidgalvggasldpvgfarivnyeat
mkrqiviagnwkmhktnseamqlanqvriktmditktqivicppftalapvyevigdsrihlgaqnmfwekegaftgeisagmikstgadyviighserrqyfgesdetvnkkvkaalenglkpivcvgetleereanitlkvvsrqirgafadlsaeqmkkvivayepvwaigtgktatpeqaqqvhqeirqlltemfgseigekmviqyggsvkpanaesllsqpdidgalvggaclkadsfseiihiaeklq
mktrqqivagnwkmnknygegrelameiverlkpsntqvvlcapyihlqlvkniikdvaslylgaqnchqedkgaytgeisvdmlksvgvsyvilghserreyfgesdellakktdkvlaagllpifccgesldirdagthvahvqaqikaglfhlspeefqkvviayepiwaigtgrtaspeqaqdmhaairalltdqygaeiadattilyggsvnggnaavlfsqpdvdgglvggaslkaeefitiveatkk
mvywvgtswkmnktlaeamdfaailagfvpgfddriqpfvippftavrqvkqalsstrvkvgaqnmhwadagawtgeispvmltdcgldlvelghserrehfgetdrtvglktaaavkhgliplicvgetlaeresgeadavlakqvegalqffeeevkgatilfayepvwaigdkgipassdyadkqqglikavagsllpsvpsvlyggsvnpgnaaeligqpnvdglfigrsawqaqgyidilgrasaai
mrryliagnwkmntsletgtalasgladhvrgrdlpvdvlvcppfpylaavkatageagisvgaqncyfeasgaftgevsvdmlkdigcdsvilghserrhvikecddminkktkaaiegglqvvlcvgelleereadkteavldeqmagglkdisaeqmtnvviayepvwaigtgktaspeqaeqahahlrkwladrytsevaeqtrilyggsvkpanakellgqqnvdgalvggasltvdnfgpiidagvelsa
mpeekpviminfktynesygfrahdiaeaaetvaeesgieivicpgfmdihpmsnhyrlpvfaqhidgispgahtghilaeavraagatgtlinhserrltladisaavdaakranlktvvctnntatsgaaaalspdyvaieppeligsgisvatadpeiiensvnavksvnkdvkvlagagissgscvkravelgsdgvllasgvvkaedpavvlrdlvski
mgsplivvnfktylegtgersvdiaracrdvaedsgvdiavapqmcdiyrvasmvdipvysqhvdgigagsftghafapaikeagasgtlinhsenrltladieaaiqaskavglktivctnniptsaaaaalspdyvaveppeligsgipvseadpdvvkgsveavmnidsgvsvlcgagiskgkdlkaaldlgskgvllasgivksedprsamedlisli
mrkkivagnwkmnldytegltlfsevinmikdevtgsqqavvcspfihlhslvqlgkdynkvsvgaqnahqaeagaytgeissrmiksvgaeyviighserrqyfgetndllakktdavlknqltpifcigetlqeretekhfeviksqllegvfhldetafaklviayepvwaigtgvtasaeqaqeihafiraeiaqkysqqvadditilyggscnpknaaelfakgdidggliggaslksrdfvdilkvfn
mdkleakesaclslsssriggmrkkliagnwkmnqtpsqavvladalkktvsgkeaaeivvcppytalipvrdalkgssihlgaqdlhwedqgaftgkisadmlldagcthviighseqrtyfhetdatvnkklikalagglvpifcigetleerdggrafdvvkkqleggfagmkdaghtvlayepvwaigtgrnatpeqaqemhafirktiaslfsaavadgmrilyggsmkpdnaagllaqpdidggliggaalkadsfygivkaag
mtpaapgapavqrrplfagnwkmhtlpaeaarlaaavregldgwgggdpaaaggtvtpgvagrgagtqpaegpagppaagaaevvlcppftslaaaaealagsaialgaqdlawgdfgaftgevsapmlrelgcryvivghserrqllgetdalilrkleaalagglvpilcvgedaaqrregrtaavvlgqaahalagldgeqaarvviayepvwaigsgtpatpadaqavaaalrglierlhgpavaaavrilyggsvkpdniggfmaqpdidgalvggasldgagfarlvrqgvaaraaapggaaageers
matsktvgrvplmagnwkmnldhlqathliqkldwtlrdakhdydgvevavlppftdlrsvqtlvegdrlhlrygaqdlsphasgaytgdisgaflkklgctyvvvghserreghhetddvvaakvqaayrhgltpilccgeglevrkegsqvehvvaqlraaldgvtreqaasiviayepiwaigtgevatpddaqevcaairtllaelysgdladgvrilyggsvkaanvaaimaqedvdgalvggasidpaefasicryrdhltag
mrtkmiagnwkmhhrpqearafveelgrvlwarnelygplkegvaeavlfptalslaavqdalgdlpvslgaqnahwedhgaftgeigapmladfgcayilighserrhlfhetevelarklravlstsarclfcvgelleereagkthqvlerqllgalegvtipdltdrfavayepvwaigtgktasdgdaeegcgyirhlvadrygqetaqhlqvlyggsvkpgntaglmvqgdidgllvggaslevpsfvgileaalgilrp

 */