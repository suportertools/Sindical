package br.com.rtools.utilitarios;

import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.DocumentoInvalidoDao;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.db.*;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.DocumentoInvalido;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ArquivoRetorno {

    private ContaCobranca contaCobranca;
//    private boolean pendentes;
    public final static int SICOB = 1;
    public final static int SINDICAL = 2;
    public final static int SIGCB = 3;
    public final static int CAIXA_FEDERAL = 109;
    public final static int REAL = 82;
    //public final static int BANESPA = 82;
    public final static int BANCO_BRASIL = 36;
    public final static int ITAU = 63;
    public final static int SANTANDER = 88;
    public final static int SICOOB = 26;

    public abstract List<GenericaRetorno> sicob(boolean baixar, String host);

    public abstract List<GenericaRetorno> sindical(boolean baixar, String host);

    public abstract List<GenericaRetorno> sigCB(boolean baixar, String host);

    public abstract String darBaixaSicob(String caminho, Usuario usuario);

    public abstract String darBaixaSigCB(String caminho, Usuario usuario);

    public abstract String darBaixaSindical(String caminho, Usuario usuario);

    public abstract String darBaixaPadrao(Usuario usuario);

    public abstract String darBaixaSicobSocial(String caminho, Usuario usuario);

    public abstract String darBaixaSigCBSocial(String caminho, Usuario usuario);

    protected ArquivoRetorno(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
//        this.pendentes = pendentes;
    }

    protected String baixarArquivo(List<GenericaRetorno> listaParametros, String caminho, Usuario usuario) {
        String cnpj = "";
        String referencia = "";
        String dataVencto = "";
        String result = "";
        String destino = caminho + "/" + DataHoje.ArrayDataHoje()[2] + "-" + DataHoje.ArrayDataHoje()[1] + "-" + DataHoje.ArrayDataHoje()[0];

        boolean moverArquivo = true;
        List<String> errors = new ArrayList();

        MovimentoDB db = new MovimentoDBToplink();
        JuridicaDao dbJur = new JuridicaDao();
        List<Movimento> movimento = new ArrayList();
        Dao dao = new Dao();
        File fl = new File(caminho + "/pendentes/");
        File listFls[] = fl.listFiles();
        File flDes = new File(destino); // 0 DIA, 1 MES, 2 ANO
        flDes.mkdir();
        TipoServico tipoServico = new TipoServico();
        // LAYOUT 2 = SINDICAL
        if (this.getContaCobranca().getLayout().getId() == 2) {
            for (int u = 0; u < listaParametros.size(); u++) {
                // VERIFICA O TIPO DA EMPRESA -------------------------------------------------------------------------------------------------
                // ----------------------------------------------------------------------------------------------------------------------------
                if (((Registro) dao.find(new Registro(), 1)).getTipoEmpresa().equals("E")) {
                    // VERIFICA O ANO QUE VEIO NO ARQUIVO MENOR QUE ANO 2000 -------------------------------------------------------
                    // -------------------------------------------------------------------------------------------------------------
                    if (Integer.parseInt(listaParametros.get(u).getDataVencimento().substring(4, 8)) < 2000) {
                        referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()));
                        dataVencto = DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento());
                        if (referencia.substring(0, 2).equals("03")) {
                            tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                        } else {
                            tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                        }
                    } else {
                        referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(listaParametros.get(u).getDataVencimento()));
                        dataVencto = DataHoje.colocarBarras(listaParametros.get(u).getDataVencimento());
                        if (referencia.substring(0, 2).equals("03")) {
                            tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                        } else {
                            tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                        }
                    }
                } else if (Integer.parseInt(listaParametros.get(u).getDataVencimento().substring(4, 8)) < 2000) {
                    referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()));
                    dataVencto = DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento());
                    if (referencia.substring(0, 2).equals("01")) {
                        tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                    } else {
                        tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                    }
                } else {
                    referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(listaParametros.get(u).getDataVencimento()));
                    dataVencto = DataHoje.colocarBarras(listaParametros.get(u).getDataVencimento());
                    if (referencia.substring(0, 2).equals("01")) {
                        tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                    } else {
                        tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                    }
                }
                // ----------------------------------------------------------------------------------------------------------------------------
                // ----------------------------------------------------------------------------------------------------------------------------

                // 1 caso VERIFICA SE EXISTE BOLETO PELO NUMERO DO BOLETO JA BAIXADO -------------------------------------------------------------------
                // -------------------------------------------------------------------------------------------------------------------
                String numeroComposto = listaParametros.get(u).getNossoNumero()
                        + listaParametros.get(u).getDataPagamento()
                        + listaParametros.get(u).getValorPago().substring(5, listaParametros.get(u).getValorPago().length());
                int nrSequencia = Integer.valueOf(listaParametros.get(u).getSequencialArquivo());

                movimento = db.pesquisaMovPorNumDocumentoListBaixadoArr(listaParametros.get(u).getNossoNumero(), this.getContaCobranca().getId());
                if (!movimento.isEmpty()) {
                    // EXISTE O BOLETO  MAS CONTEM VALORES DIFERENTES --------------
                    Movimento mov2 = movimento.get(0);

                    Servicos servicos = (Servicos) (new Dao()).find(new Servicos(), 1);

                    movimento = db.pesquisaMovPorNumPessoaListBaixado(numeroComposto, this.getContaCobranca().getId());
                    if (movimento.isEmpty()) {
                        Movimento movi = new Movimento(-1,
                                null,
                                servicos.getPlano5(),
                                mov2.getPessoa(),
                                servicos,
                                null,
                                tipoServico,
                                null,
                                0,
                                referencia,
                                dataVencto,
                                1,
                                true,
                                "E",
                                false,
                                mov2.getPessoa(),
                                mov2.getPessoa(),
                                numeroComposto,
                                "",
                                dataVencto,
                                0, 0, 0, 0, 0,
                                Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100),
                                Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100),
                                (FTipoDocumento) (new Dao()).find(new FTipoDocumento(), 2),
                                0, null);

                        if (GerarMovimento.salvarUmMovimentoBaixa(new Lote(), movi)) {
                            float valor_liquido = Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorCredito())), 100);
                            GerarMovimento.baixarMovimento(
                                    movi,
                                    usuario,
                                    DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()),
                                    valor_liquido,
                                    DataHoje.converte(DataHoje.colocarBarras(listaParametros.get(u).getDataCredito())),
                                    numeroComposto, nrSequencia);
                        }
                    } else if (movimento.get(0).getBaixa().getSequenciaBaixa() == 0) {
                        movimento.get(0).getBaixa().setSequenciaBaixa(nrSequencia);
                        dao = new Dao();
                        dao.openTransaction();
                        if (dao.update(movimento.get(0).getBaixa())) {
                            dao.commit();
                        } else {
                            dao.rollback();
                        }
                    }
                    movimento.clear();
                    continue;
                }

                // 2 caso VERIFICA SE EXISTE BOLETO PELO NUMERO DO BOLETO AINDA NÃO BAIXADO -------------------------------------------------------------------
                movimento = db.pesquisaMovPorNumDocumentoListSindical(listaParametros.get(u).getNossoNumero(), this.getContaCobranca().getId());
                if (!movimento.isEmpty()) {
                    // ENCONTROU O BOLETO PRA BAIXAR
                    movimento.get(0).setValorBaixa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100));
                    movimento.get(0).setTaxa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100));

                    float valor_liquido = Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorCredito())), 100);
                    GerarMovimento.baixarMovimento(
                            movimento.get(0),
                            usuario,
                            DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()),
                            valor_liquido,
                            DataHoje.converte(DataHoje.colocarBarras(listaParametros.get(u).getDataCredito())),
                            numeroComposto, nrSequencia);
                    continue;
                }

                // 3 caso VERIFICA SE EXISTE BOLETO PELO CNPJ DA EMPRESA + DATA DE PAGAMENTO + VALOR PAGO BAIXADO ---------------------------
                // ------------------------------------------------------------------------------------------------------
                movimento = db.pesquisaMovPorNumPessoaListBaixado(numeroComposto, this.getContaCobranca().getId());

                if (!movimento.isEmpty()) {
                    // EXISTE O BOLETO PELO CNPJ DA EMPRESA + DATA DE PAGAMENTO BAIXADO --------------
                    movimento.clear();
                    continue;
                }

                List<Juridica> listJuridica = dbJur.pesquisaJuridicaParaRetorno(listaParametros.get(u).getNossoNumero());

                if (!listJuridica.isEmpty()) {
                    movimento = db.pesquisaMovimentoChaveValor(listJuridica.get(0).getPessoa().getId(), referencia, this.getContaCobranca().getId(), tipoServico.getId());

                    if (!movimento.isEmpty()) {
                        movimento.get(0).setValorBaixa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100));
                        movimento.get(0).setTaxa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100));

                        float valor_liquido = Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorCredito())), 100);
                        GerarMovimento.baixarMovimento(
                                movimento.get(0),
                                usuario,
                                DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()),
                                valor_liquido,
                                DataHoje.converte(DataHoje.colocarBarras(listaParametros.get(u).getDataCredito())),
                                numeroComposto, nrSequencia);
                        continue;
                    }

                    Servicos servicos = (Servicos) (new Dao()).find(new Servicos(), 1);
                    Movimento movi = new Movimento(-1,
                            null,
                            servicos.getPlano5(),
                            listJuridica.get(0).getPessoa(),
                            servicos,
                            null,
                            tipoServico,
                            null,
                            0,
                            referencia,
                            dataVencto,
                            1,
                            true,
                            "E",
                            false,
                            listJuridica.get(0).getPessoa(),
                            listJuridica.get(0).getPessoa(),
                            listaParametros.get(u).getNossoNumero()
                            + listaParametros.get(u).getDataPagamento()
                            + listaParametros.get(u).getValorPago().substring(5, listaParametros.get(u).getValorPago().length()),
                            "",
                            dataVencto,
                            0, 0, 0, 0, 0,
                            Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100),
                            Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100),
                            (FTipoDocumento) (new Dao()).find(new FTipoDocumento(), 2),
                            0,
                            null);

                    if (GerarMovimento.salvarUmMovimentoBaixa(new Lote(), movi)) {
                        float valor_liquido = Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorCredito())), 100);
                        GerarMovimento.baixarMovimento(
                                movi,
                                usuario,
                                DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()),
                                valor_liquido,
                                DataHoje.converte(DataHoje.colocarBarras(listaParametros.get(u).getDataCredito())),
                                numeroComposto, nrSequencia);
                    }
                } else {

                    Servicos servicos = (Servicos) (new Dao()).find(new Servicos(), 1);
                    Movimento movi = new Movimento(-1,
                            null,
                            servicos.getPlano5(),
                            (Pessoa) dao.find(new Pessoa(), 0),
                            servicos,
                            null,
                            tipoServico,
                            null,
                            0,
                            referencia,
                            dataVencto,
                            1,
                            true,
                            "E",
                            false,
                            (Pessoa) dao.find(new Pessoa(), 0),
                            (Pessoa) dao.find(new Pessoa(), 0),
                            listaParametros.get(u).getNossoNumero()
                            + listaParametros.get(u).getDataPagamento()
                            + listaParametros.get(u).getValorPago().substring(5, listaParametros.get(u).getValorPago().length()),
                            "",
                            dataVencto,
                            0, 0, 0, 0, 0,
                            Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100),
                            Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100),
                            (FTipoDocumento) (new Dao()).find(new FTipoDocumento(), 2), 0, null);

                    if (GerarMovimento.salvarUmMovimentoBaixa(new Lote(), movi)) {
                        float valor_liquido = Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorCredito())), 100);

                        DocumentoInvalidoDao dbDocInv = new DocumentoInvalidoDao();
                        List<DocumentoInvalido> listaDI = dbDocInv.pesquisaNumeroBoleto(listaParametros.get(u).getNossoNumero());

                        if (listaDI.isEmpty()) {
                            dao = new Dao();
                            DocumentoInvalido di = new DocumentoInvalido(-1, listaParametros.get(u).getNossoNumero(), false, DataHoje.data());

                            dao.openTransaction();
                            if (dao.save(di)) {
                                dao.commit();
                            } else {
                                dao.rollback();
                            }
                        }

                        GerarMovimento.baixarMovimento(
                                movi,
                                usuario,
                                DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()),
                                valor_liquido,
                                DataHoje.converte(DataHoje.colocarBarras(listaParametros.get(u).getDataCredito())),
                                numeroComposto, nrSequencia);
                    }
                }
            }
        } else {
            // SE NÃO FOR SINDICAL ---------------------------
            for (int u = 0; u < listaParametros.size(); u++) {
                if (cnpj.equals("")) {
                    cnpj = AnaliseString.mascaraCnpj(listaParametros.get(0).getCnpj());
                    if (dbJur.pesquisaJuridicaPorDoc(cnpj).isEmpty()) {
                        errors.add(" Documento não Existe no Sistema! " + listaParametros.get(u).getCnpj());
                        //return " Documento não Existe no Sistema! "+ listaParametros.get(u).getCnpj();
                    }
                }
                movimento = db.pesquisaMovPorNumDocumentoList(listaParametros.get(u).getNossoNumero(),
                        DataHoje.converte(DataHoje.colocarBarras(listaParametros.get(u).getDataVencimento())),
                        this.getContaCobranca().getId());
                if (!movimento.isEmpty()) {
                    if (movimento.size() == 1) {
                        movimento.get(0).setValorBaixa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100));
                        movimento.get(0).setTaxa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100));

                        GerarMovimento.baixarMovimento(movimento.get(0), usuario, DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()), 0, null, "", 0);
                    }
                }
                movimento = new ArrayList();
            }
        }

        // TERMINAR O CASO DE PENDENTES OU NÃO --------------------------------
        if (listFls != null) {
            if (moverArquivo) {
                for (int i = 0; i < listFls.length; i++) {
                    flDes = new File(caminho + "/pendentes/" + listFls[i].getName());

                    fl = new File(destino + "/" + listFls[i].getName());
                    if (fl.exists()) {
                        fl.delete();
                    }

                    if (!flDes.renameTo(fl)) {
                        result = " Erro ao mover arquivo!";
                    }
                }
            }
        }
        return result;
    }

    protected String baixarArquivoPadrao(List<GenericaRetorno> listaParametros, Usuario usuario) {
        return "Processo Concluido []";
    }

    protected String baixarArquivoSocial(List<GenericaRetorno> listaParametros, String caminho, Usuario usuario) {
        String cnpj = "";
        String result = "";
        String destino = caminho + "/" + DataHoje.ArrayDataHoje()[2] + "-" + DataHoje.ArrayDataHoje()[1] + "-" + DataHoje.ArrayDataHoje()[0];

        boolean moverArquivo = true;
        List<String> errors = new ArrayList();

        MovimentoDB db = new MovimentoDBToplink();
        JuridicaDao dbJur = new JuridicaDao();
        List<Movimento> lista_movimento = new ArrayList();
        File fl = new File(caminho + "/pendentes/");
        File listFls[] = fl.listFiles();
        File flDes = new File(destino); // 0 DIA, 1 MES, 2 ANO
        flDes.mkdir();

        List<Object[]> lista_logs = new ArrayList();
        // LAYOUT 2 = SINDICAL
        if (this.getContaCobranca().getLayout().getId() != 2) {
            for (int u = 0; u < listaParametros.size(); u++) {
                if (cnpj.equals("")) {
                    cnpj = AnaliseString.mascaraCnpj(listaParametros.get(0).getCnpj());
                    if (dbJur.pesquisaJuridicaPorDoc(cnpj).isEmpty()) {
                        errors.add(" Documento não Existe no Sistema! " + listaParametros.get(u).getCnpj());
                    }
                }

                lista_movimento = db.pesquisaMovPorNumDocumentoListBaixadoAss(listaParametros.get(u).getNossoNumero(), this.getContaCobranca().getId());
                if (lista_movimento.isEmpty()) {
                    lista_movimento = db.pesquisaMovPorNumDocumentoListAss(listaParametros.get(u).getNossoNumero(), this.getContaCobranca().getId());
                    if (!lista_movimento.isEmpty()) {
                        //movimento.get(0).setValorBaixa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100));
                        //movimento.get(0).setTaxa(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100));

                        // logs de mensagens ---
                        // 0 - ERRO AO INSERIR BAIXA - [1] obj Baixa
                        // 1 - ERRO AO INSERIR FORMA DE PAGAMENTO - [1] obj FormaPagamento
                        // 2 - ERRO AO ALTERAR MOVIMENTO COM A BAIXA - [1] obj Movimento
                        // 3 - ERRO AO ALTERAR MOVIMENTO COM DESCONTO E VALOR BAIXA - [1] obj Movimento
                        // 4 - ERRO AO ALTERAR MOVIMENTO COM CORREÇÃO E VALOR BAIXA - [1] obj Movimento
                        // 5 - BAIXA CONCLUÍDA COM SUCESSO
                        // 6 - VALOR DA BAIXA MENOR - [1] obj Lista Movimento
                        // 7 - VALOR DA BAIXA MAIOR - [1] obj Lista Movimento
                        // 8 - BOLETO NÃO ENCONTRADO - [1] string Número do Boleto
                        // 9 - ERRO AO ALTERAR MOVIMENTO COM VALOR BAIXA CORRETO- [1] obj Movimento
                        Object[] log = GerarMovimento.baixarMovimentoSocial(
                                lista_movimento, // lista de movimentos
                                usuario, // usuario que esta baixando
                                DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento()), // data do pagamento
                                Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100), // valor liquido ( total pago )
                                Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100) // valor taxa
                        );

                        lista_logs.add(log);
                    } else {
                        Object[] log = new Object[3];

                        log[0] = 8;
                        log[1] = listaParametros.get(u).getNossoNumero();
                        log[2] = "Boleto não Encontrado - " + listaParametros.get(u).getNossoNumero()
                                + " - Data de Vencimento: " + DataHoje.colocarBarras(listaParametros.get(u).getDataVencimento())
                                + " - Data de Pagamento: " + DataHoje.colocarBarras(listaParametros.get(u).getDataPagamento())
                                + " - Valor Pago: " + Moeda.converteR$Float(Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(((GenericaRetorno) listaParametros.get(u)).getValorPago())), 100));
                        lista_logs.add(log);
                    }
                }
                lista_movimento.clear();
            }
        }

        GenericaSessao.put("logsRetornoSocial", lista_logs);

        if (listFls != null) {
            if (moverArquivo) {
                for (int i = 0; i < listFls.length; i++) {
                    flDes = new File(caminho + "/pendentes/" + listFls[i].getName());

                    fl = new File(destino + "/" + listFls[i].getName());
                    if (fl.exists()) {
                        fl.delete();
                    }

                    if (!flDes.renameTo(fl)) {
                        result = " Erro ao mover arquivo!";
                    }
                }
            }
        }
        return result;
    }

    public ContaCobranca getContaCobranca() {
        return contaCobranca;
    }

    public void setContaCobranca(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }
//
//    public boolean isPendentes() {
//        return pendentes;
//    }
//
//    public void setPendentes(boolean pendentes) {
//        this.pendentes = pendentes;
//    }
}
