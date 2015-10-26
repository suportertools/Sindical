package br.com.rtools.seguranca.beans;

import br.com.rtools.seguranca.Log;
import br.com.rtools.seguranca.dao.PesquisaLogDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class LogHistoryBean implements Serializable {

    private List<Log> listLogs;

    public void load(String tabela, Integer codigo) {
        listLogs = new ArrayList();
        listLogs = new PesquisaLogDao().find(tabela, codigo);
    }

    public List<Log> getListLogs() {
        return listLogs;
    }

    public void setListLogs(List<Log> listLogs) {
        this.listLogs = listLogs;
    }

}
