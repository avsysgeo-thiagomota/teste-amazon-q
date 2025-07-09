// ReceitaStore.js

Ext.ns('App.store');

App.store.Receitas = Ext.extend(Ext.data.JsonStore, {
    constructor: function(config){
        config = config || {};
        App.store.Receitas.superclass.constructor.call(this, Ext.apply({
            // URL da sua API que lista os dados
            url: 'receitas.jsp?action=listar',

            // IMPORTANTE: Seu JSON de resposta do servidor deve ter este formato:
            // {
            //   "total": 25,
            //   "receitas": [ {..receita1..}, {..receita2..} ]
            // }
            root: 'receitas',
            totalProperty: 'total',

            // Define a estrutura dos dados usando o Model que criamos
            fields: App.model.Receita,

            remoteSort: true
        }, config));
    }
});