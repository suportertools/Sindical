package br.com.rtools.utilitarios;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Classe que padroniza a internacionalizacao de valores monetarios
 *
 * @version 0.1
 * @see java.util.Locale
 * @see java.text.DecimalFormat
 * @see java.text.DecimalFormatSymbols
 */
public final class Moeda {

    /**
     * Simbolos especificos do Dolar Americano
     */
    private static final DecimalFormatSymbols DOLAR = new DecimalFormatSymbols(Locale.US);
    /**
     * Mascara de dinheiro para Dolar Americano
     */
    public static final DecimalFormat DINHEIRO_DOLAR = new DecimalFormat("###,###,##0.00", DOLAR);
    /**
     * Simbolos especificos do Euro
     */
    private static final DecimalFormatSymbols EURO = new DecimalFormatSymbols(Locale.GERMANY);
    /**
     * Mascara de dinheiro para Euro
     */
    public static final DecimalFormat DINHEIRO_EURO = new DecimalFormat("� ###,###,##0.00", EURO);
    /**
     * Locale Brasileiro
     */
    private static final Locale BRAZIL = new Locale("pt", "BR");
    /**
     * S�mbolos especificos do Real Brasileiro
     */
    private static final DecimalFormatSymbols REAL = new DecimalFormatSymbols(BRAZIL);
    /**
     * Mascara de dinheiro para Real Brasileiro
     */
    public static final DecimalFormat DINHEIRO_REAL = new DecimalFormat("###,###,##0.00", REAL);

    /**
     * Mascara texto com formatacao monetaria
     *
     * @param valor Valor a ser mascarado
     * @param moeda Padrao monetario a ser usado
     * @return Valor mascarado de acordo com o padrao especificado Ex: String m
     * = Moeda.mascaraDinheiro(100,Moeda.DINHEIRO_REAL); m = R$ 100.00
     */
    public static String mascaraDinheiro(double valor, DecimalFormat moeda) {
        return moeda.format(valor);
    }

    //Converte Campo Real para campo Dolar
    public static float converteUS$(String $dolar) {
        return converteUS$($dolar, null);
    }

    public static float converteUS$(String $dolar, Integer decimal) {
        try {
            if ($dolar == null || $dolar.isEmpty()) {
                $dolar = "0,00";
            }
            if (decimal != null && decimal > 2) {
                if ($dolar.length() >= 3) {
                    String[] splitter = $dolar.split("\\.");
                    if (splitter.length == 1) {
                        splitter = $dolar.split("\\,");
                    }
                    String wponto = "";
                    if (splitter[1].length() == 3) {
                        wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
                    } else {
                        wponto = $dolar.substring($dolar.trim().length() - 5, $dolar.trim().length() - 4);
                    }
                    if (wponto.equals(",")) {
                        $dolar = $dolar.replace(".", "");
                        $dolar = $dolar.replace(",", ".");
                    }
                }
            } else if ($dolar.length() >= 3) {
                String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
                if (wponto.equals(",")) {
                    $dolar = $dolar.replace(".", "");
                    $dolar = $dolar.replace(",", ".");
                }
            }
            return Float.parseFloat($dolar);
        } catch (Exception e) {
            return converteUS$($dolar, 4);
        }
    }

    public static String converteR$(String $dolar) {
        //$dolar = $dolar.replaceAll("[^0-9]", "");
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return converteR$($dolar, 0);
    }

    public static String converteR$(String $dolar, Integer decimal) {
        //$dolar = $dolar.replaceAll("[^0-9]", "");
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if (decimal != null && decimal > 2) {
            DecimalFormat df = new DecimalFormat("###,###,##0.0000", REAL);
            if ($dolar.length() >= 3) {
                String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
                if (!wponto.equals(",")) {
                    $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), df);
                }
            } else {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), df);
            }
        } else if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return $dolar;
    }

    public static String converteR$Double(double valor) {
        String $dolar = Double.toString(valor);
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return $dolar;
    }

    public static String converteR$Float(float valor) {
        return converteR$Float(valor, null);
    }

    public static String converteR$Float(float valor, Integer decimal) {
        String $dolar = Float.toString(valor);
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if (decimal != null && decimal > 2) {
            DecimalFormat df = new DecimalFormat("###,###,##0.0000", REAL);
            if ($dolar.length() >= 3) {
                String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
                if (!wponto.equals(",")) {
                    $dolar = Moeda.mascaraDinheiro(Float.parseFloat($dolar), df);
                }
            } else {
                $dolar = Moeda.mascaraDinheiro(Float.parseFloat($dolar), df);
            }
        } else if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                DecimalFormat moeda = new DecimalFormat("###,###,##0.00", REAL);
                // String d = moeda.format(Double.parseDouble($dolar), moeda);
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Float.parseFloat($dolar), Moeda.DINHEIRO_REAL);
        }
        return $dolar;
    }

    public static Float converteFloatR$Float(float valor) {
        String $dolar = Float.toString(valor);
        if ($dolar == null || $dolar.isEmpty()) {
            return (float) 0.0;
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Float.parseFloat($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Float.parseFloat($dolar), Moeda.DINHEIRO_REAL);
        }
        return Moeda.substituiVirgulaFloat($dolar);
    }

    public static String substituiVirgula(String v) {
        if (v.indexOf(",") == -1) {
            return v;
        }
        v = v.replace(".", "");
        v = v.replace(",", ".");
        return v;
    }

    public static float substituiVirgulaFloat(String v) {
        if (v.indexOf(",") == -1) {
            return Float.parseFloat(v);
        }
        v = v.replace(".", "");
        v = v.replace(",", ".");
        return Float.parseFloat(v);
    }

    public static float somaValores(float a, float b) {
        BigDecimal aBig = new BigDecimal(Float.toString(a));
        BigDecimal bBig = new BigDecimal(Float.toString(b));
        return (aBig.add(bBig)).floatValue();
    }

    public static Float somaValores(Float[] f) {
        try {
            float t = 0;
            for (int i = 0; i < f.length; i++) {
                t += (float) f[i];
            }
            return new BigDecimal(Float.toString(t)).floatValue();
        } catch (Exception e) {
            return (float) 0;
        }
    }

    public static String incremento(String a, String b) { // a = boleto somado   ,  b = 1
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
        BigDecimal result = aBig.add(bBig);
        /*  BigDecimal potencia = null; // NÃO APAGAR
         if ((aBig.toString().length() - result.toString().length()) > 1){
         potencia = bBig.scaleByPowerOfTen(result.toString().length() -1);
         }else{
         potencia = bBig.scaleByPowerOfTen((result.toString().length() -1) * -1);
         result = result.multiply(bBig.scaleByPowerOfTen((result.toString().length() -1) * -1));
         }*/

        //return result.subtract(potencia).toString();
        return result.toString().substring(1);
    }

    // NOVOS METODOS CLAUDEMIR --------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    public static double soma(double a, double b) {
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
        return aBig.add(bBig).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    public static double subtracao(double a, double b) {
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
//        BigDecimal aBig = new BigDecimal(753.71);
//        BigDecimal bBig = new BigDecimal(0.02);
        return aBig.subtract(bBig).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    public static double multiplicar(double a, double b) {
        BigDecimal aBig = new BigDecimal(a, new MathContext(2));
        BigDecimal bBig = new BigDecimal(b, new MathContext(2));
        return aBig.multiply(bBig).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    public static double divisao(double a, double divisor) {
        try {
            BigDecimal aBig = new BigDecimal(a, new MathContext(2));
            BigDecimal bBig = new BigDecimal(divisor, new MathContext(2));
            return aBig.divide(bBig, new MathContext(100)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String converteDoubleToString(Double d) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
//        // Formato com sinal de menos -5.000,00
//        DecimalFormat df1 = new DecimalFormat ("#,##0.00", dfs);
        // Formato com parêntese (5.000,00)
        DecimalFormat df2 = new DecimalFormat("#,##0.00;#,##0.00", dfs);
        //d = -5000.00;
//        s = df1.format (d); 
//        System.out.println (s); // imprime -5.000,00
        String s = df2.format(d);
        System.out.println(); // imprime (5.000,00)

        return s;
    }

    public static Double converteStringToDouble(String s) {
        try {
            DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols sfs = new DecimalFormatSymbols();
            sfs.setDecimalSeparator(',');
            df.setDecimalFormatSymbols(sfs);
            return df.parse(s).doubleValue();
        } catch (Exception e) {
            return new Double(0);
        }
    }

    // FIM NOVOS METODOS CLAUDEMIR --------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    public static float subtracaoValores(float a, float b) {
        BigDecimal aBig = new BigDecimal(Float.toString(a));
        BigDecimal bBig = new BigDecimal(Float.toString(b));
        return (aBig.subtract(bBig)).floatValue();
    }

    public static float multiplicarValores(float a, float b) {
        BigDecimal aBig = new BigDecimal(Float.toString(a));
        BigDecimal bBig = new BigDecimal(Float.toString(b));
        return (aBig.multiply(bBig)).floatValue();
    }

    public static float divisaoValores(float a, float divisor) {
        try {
            BigDecimal aBig = new BigDecimal(Float.toString(a));
            BigDecimal bBig = new BigDecimal(Float.toString(divisor));
            String result = aBig.divide(bBig, new MathContext(100)).toString();
            return Float.parseFloat(result);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String limparPonto(String valor) {
        valor = converteR$(valor);
        valor = substituiVirgula(valor);
        int i = 0;
        String result = "";
        while (i < valor.length()) {
            if (valor.charAt(i) != '.') {
                result += valor.charAt(i);
            } else if (((i + 2) > valor.length()) && (valor.charAt(i + 1) == '0')) {
                i++;
            }
            i++;
        }
        return result;
    }

    public static String limparVirgula(String valor) {
        int i = 0;
        String result = "";
        while (i < valor.length()) {
            if (valor.charAt(i) != ',') {
                result += valor.charAt(i);
            } else if ((i + 2) > valor.length()) {
                result += valor.charAt(i + 1);
                result += valor.charAt(i + 2);
                break;
            }
            i++;
        }
        return result;
    }

//    public static String percentualDoValor(String valorFixo, String valorCalculo) {
//        float v1 = Moeda.subtracaoValores(Moeda.converteUS$(valorFixo), Moeda.converteUS$(valorCalculo));
//        float v2 = Moeda.multiplicarValores(Moeda.divisaoValores(v1, Moeda.converteUS$(valorFixo)), 100);
//        return Moeda.converteR$Float(v2);
//    }
    public static String percentualDoValor(String valorFixo, String valorCalculo) {
        double v1 = Moeda.converteUS$(valorFixo);
        double v2 = Moeda.converteUS$(valorCalculo);
        return Double.toString((v2 / v1) * 100);
    }

    public static String valorDoPercentual(String valorFixo, String percentual) {
        //v = servicoValorDetalhe.getValor() - (listServicosCategoriaDesconto.get(i).getCategoriaDesconto().getDesconto() / 100) * servicoValorDetalhe.getValor();
        float v = Moeda.converteUS$(valorFixo) - (Moeda.converteUS$(percentual) / 100) * Moeda.converteUS$(valorFixo);
        return Moeda.converteR$Float(v);
    }

    public static Float percentualDoValor(Float valorFixo, Float valorCalculo) {
        double v1 = valorFixo;
        double v2 = valorCalculo;
        return Moeda.converteUS$(Double.toString((v2 / v1) * 100));
    }

    public static Float valorDoPercentual(Float valorFixo, Float percentual) {
        float v = valorFixo - (percentual / 100) * valorFixo;
        return v;
    }

//
//    public static String valorDoPercentual(String valorFixo, String percentual) {
//        //float v = Moeda.converteUS$(valorFixo) - (Moeda.converteUS$(percentual) / 100) * Moeda.converteUS$(valorFixo);
//        float v = Moeda.subtracaoValores(Moeda.converteUS$(valorFixo), Moeda.multiplicarValores((Moeda.divisaoValores(Moeda.converteUS$(percentual), 100)), Moeda.converteUS$(valorFixo)));
//        return Moeda.converteR$Float(v);
//    }
}
