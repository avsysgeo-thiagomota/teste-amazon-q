Ext.ns('App.store');

App.store.Receitas = Ext.extend(Ext.data.JsonStore, {
    constructor: function(config){
        config = config || {};
        App.store.Receitas.superclass.constructor.call(this, Ext.apply({
            url: 'receitas.jsp?action=listar',
            root: 'receitas',
            totalProperty: 'total',
            fields: App.model.Receita,
            remoteSort: true
        }, config));
    }
});