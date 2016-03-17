package AlgoritmoPuzzle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

//public class QuebraCabeca {
public class AlgoritmoPuzzle {

    static int CIMA = 1;
    static int BAIXO = 2;
    static int DIREITA = 3;
    static int ESQUERDA = 4;

    static int dimensao = 3;
    static int tamanho_fila = 0;
    static boolean printOn = true;
    static int nos_expandidos = 0;
    static int tamanho_fila_max = 0;
    static double custo_solucao = 0.0;

    static int[] solucao = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 0};
    static int[] inicio = new int[]{8, 4, 0, 5, 1, 7, 6, 2, 3};

    static PriorityQueue fila = new PriorityQueue(5, (Comparator) new ComparadorNo());
    static Set processados = new HashSet();

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        No no = iniciarBusca(inicio);
        if (no != null) {
            imprimirResultado(no, true);
        } else {
            System.out.println("SOLUCAO NAO ENCONTRADA!");
        }
        System.out.println("\r\n\r\nElapsed time=" + (System.currentTimeMillis() - l) + "ms");
    }

    private static No iniciarBusca(int[] posicoesIniciais) {
        No noInicial = new No();
        noInicial.estado = posicoesIniciais;
        fila.add(noInicial);
        tamanho_fila = 1;
        while (!(fila.isEmpty())) {
            tamanho_fila--;
            No no = (No) fila.remove();
            if (goal(no.estado)) {
                custo_solucao = no.custoCaminho;
                return no;
            }
            adicionarNosAlternativosFila(no);
            tamanho_fila = fila.size();
            if (tamanho_fila_max < tamanho_fila) {
                tamanho_fila_max = tamanho_fila; // estatistica
            }
        }
        return null; // falhou
    }

    private static void imprimirResultado(No no, boolean imprimeTabuleiros) {
        print("ESTADO FINAL:");
        imprimirTabuleiro(no);
        print("tamanho_fila_max = " + tamanho_fila_max);
        print("nos_expandidos = " + nos_expandidos);
        print("OPERACOES :" + no.step);

        while (no.pai != null) {
            print("");
            print("Estado: " + no.step);
            print("Custo:" + no.custoCaminho);
            print("Acao:" + getAcaoReversa(no.acao));
            no = no.pai;
            if (imprimeTabuleiros) {
                imprimirTabuleiro(no);
            }
        }
    }

    private static String getAcaoReversa(int acao) {
        switch (acao) {
            case 1:
                return "BAIXO";
            case 2:
                return "CIMA";
            case 3:
                return "ESQUERDA";
            case 4:
                return "DIREITA";
        }
        return "NENHUMA";
    }

    private static void imprimirTabuleiro(No no) {
        if (!printOn) {
            return;
        }
        for (int i = 0; i < dimensao; i++) {
            imprimirTabuleiroLinha();
            for (int j = 0; j < dimensao; j++) {
                int n = i * dimensao + j;
                System.out.print(" " + no.estado[n] + " ");
            }
        }
        imprimirTabuleiroLinha();
    }

    private static void imprimirTabuleiroLinha() {
        if (!printOn) {
            return;
        }
        System.out.println("");
    }

    private static void print(String s) {
        if (printOn) {
            System.out.println(s);
        }
    }

    //heuristica
    private static int heuristica(int[] estado) {
        int valor = 0;
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                int n = i * dimensao + j;
                valor += estado[n] == solucao[n] ? 1 : 0;
            }
        }
        return valor;
    }

    private static boolean goal(int[] estado) {
        for (int i = 0; i < dimensao * dimensao; i++) {
            if (estado[i] != solucao[i]) {
                return false;
            }
        }
        return true;
    }

    private static List recuperarSucessores(int[] estado) {
        List filhos = new ArrayList();
        if (podeMoverCalhau(estado, CIMA)) {
            int[] novoEstado = clonar(estado);
            moverCima(novoEstado);
            filhos.add(new Sucessor(novoEstado, CIMA));
        }
        if (podeMoverCalhau(estado, ESQUERDA)) {
            int[] novoEstado = clonar(estado);
            moverEsquerda(novoEstado);
            filhos.add(new Sucessor(novoEstado, ESQUERDA));
        }
        if (podeMoverCalhau(estado, DIREITA)) {
            int[] novoEstado = clonar(estado);
            moverDireita(novoEstado);
            filhos.add(new Sucessor(novoEstado, DIREITA));
        }
        if (podeMoverCalhau(estado, BAIXO)) {
            int[] novoEstado = clonar(estado);
            moverBaixo(novoEstado);
            filhos.add(new Sucessor(novoEstado, BAIXO));
        }
        return filhos;
    }

    private static void moverCima(int[] estado) {
        int pos = 0;
        for (int i = dimensao; i < dimensao * dimensao; i++) {
            if (estado[i] == 0) {
                pos = i;
                break;
            }
        }
        if (pos > 0) {
            int valor = estado[pos - dimensao];
            estado[pos - dimensao] = 0;
            estado[pos] = valor;
        }
    }

    private static void moverBaixo(int[] estado) {
        int pos = 0;
        for (int i = 0; i < dimensao * dimensao; i++) {
            if (estado[i] == 0) {
                pos = i;
                break;
            }
        }
        int valor = estado[pos + dimensao];
        estado[pos + dimensao] = 0;
        estado[pos] = valor;
    }

    private static void moverEsquerda(int[] estado) {
        int pos = 0;
        for (int i = 0; i < dimensao * dimensao; i++) {
            if (estado[i] == 0) {
                pos = i;
                break;
            }
        }
        int valor = estado[pos - 1];
        estado[pos - 1] = 0;
        estado[pos] = valor;
    }

    private static void moverDireita(int[] estado) {
        int pos = 0;
        for (int i = 0; i < dimensao * dimensao; i++) {
            if (estado[i] == 0) {
                pos = i;
                break;
            }
        }
        int valor = estado[pos + 1];
        estado[pos + 1] = 0;
        estado[pos] = valor;
    }

    private static void adicionarNosAlternativosFila(No no) {
        if (!(processados.contains(no.toString()))) {
            processados.add(no.toString());
            List<No> expandidos = expandirNos(no);
            for (No o : expandidos) {
                fila.add(o);
            }
        }
    }

    private static int[] clonar(int[] estado) {
        int[] ret = new int[estado.length];
        for (int i = 0; i < estado.length; i++) {
            ret[i] = estado[i];
        }
        return ret;
    }

    private static List expandirNos(No no) {
        List nos = new ArrayList();
        List<Sucessor> proximos = recuperarSucessores(no.estado);
        for (Sucessor prox : proximos) {
            No no0 = new No();
            no0.pai = no;
            no0.estado = prox.estado;
            no0.step = no.step + 1;
            no0.acao = prox.acao;
            // o custo é sempre 1 pois movemos 1 bloco a cada passo
            no0.custoStep = 1.0;
            no0.custoCaminho += no0.pai.custoCaminho + 1.0;
            nos.add(no0);
        }
        nos_expandidos++;
        return nos;
    }

    private static boolean podeMoverCalhau(int[] estado, int acao) {
        int posicao = 0;
        for (int i = 0; i < dimensao * dimensao; i++) {
            if (estado[i] == 0) {
                posicao = i;
                break;
            }
        }
        if (acao == ESQUERDA) {
            while (posicao >= 0) {
                if (posicao == 0) {
                    return false;
                }
                posicao -= dimensao;
            }
        } else if (acao == CIMA) {
            if (posicao >= 0 && posicao < dimensao) {
                return false;
            }
        } else if (acao == DIREITA) {
            posicao++;
            while (posicao >= dimensao) {
                if (posicao == dimensao) {
                    return false;
                }
                posicao -= dimensao;
            }
        } else if (acao == BAIXO) {
            if (posicao >= dimensao * (dimensao - 1)) {
                return false;
            }
        }
        return true;
    }

    static class Sucessor {

        public Sucessor(int _estado[], int _acao) {
            estado = _estado;
            acao = _acao;
        }

        public int[] estado;
        public int acao;
    }

    static class No {

        public int[] estado;
        public int acao;
        public No pai;
        public int step = 0;
        public double custoCaminho = 0.0;
        public double custoStep = 0.0;

        public List recuperarArvore() {
            No atual = this;
            List ret = new ArrayList();
            while (!(atual.pai != null)) {
                ret.add(0, atual);
                atual = atual.pai;
            }
            ret.add(0, atual);
            return ret;
        }

        @Override
        public String toString() {
            String ret = "";
            for (int i = 0; i < dimensao * dimensao; i++) {
                ret += estado[i];
            }
            return ret;
        }

        @Override
        public boolean equals(Object o) {
            if ((o == null) || (this.getClass() != o.getClass())) {
                return false;
            }
            if (this == o) {
                return true;
            }
            No x = (No) o;
            for (int i = 0; i < dimensao; i++) {
                if (estado[i] != x.estado[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    static class ComparadorNo implements Comparator {

        @Override
        public int compare(Object arg0, Object arg1) {

            return 0;
        }
    }
}
