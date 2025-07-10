Ext.ns('App.store');

App.store.Receitas = Ext.extend(Ext.data.JsonStore, {
    constructor: function(config){
        config = config || {};
        App.store.Receitas.superclass.constructor.call(this, Ext.apply({
            // Aponta para a nossa servlet
            url: 'receitas?action=listar',

            // Define como o JSON de resposta será lido
            root: 'receitas',
            totalProperty: 'total',

            fields: App.model.Receita,
            remoteSort: true
        }, config));
    }
});