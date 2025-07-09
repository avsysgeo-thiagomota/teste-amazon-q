Ext.ns('App.view');

App.view.ReceitaGrid = Ext.extend(Ext.grid.GridPanel, {

    constructor: function(config){
        config = config || {};

        // Instancia o store
        this.store = new App.store.Receitas({ autoLoad: true });

        App.view.ReceitaGrid.superclass.constructor.call(this, Ext.apply({
            title: 'Gerenciador de Receitas',
            width: '100%',
            height: Ext.getBody().getViewSize().height,
            loadMask: true,
            stripeRows: true,
            columns: [
                {header: 'ID', width: 50, dataIndex: 'id', sortable: true},
                {header: 'Nome', id: 'nome_col', dataIndex: 'nome', sortable: true},
                {header: 'Dificuldade', width: 120, dataIndex: 'dificuldade', sortable: true},
                {header: 'Tempo (min)', width: 120, dataIndex: 'tempoDePreparo', sortable: true, align: 'right'},
                {header: 'Porções', width: 100, dataIndex: 'porcoes', sortable: true, align: 'right'},
                {
                    xtype: 'actioncolumn', width: 50,
                    items: [
                        // *** CORREÇÃO: Voltamos a usar a propriedade 'icon' ***
                        {
                            icon: 'extjs/resources/images/default/dd/drop-add.gif', // Caminho para o ícone de edição
                            tooltip: 'Editar Receita',
                            handler: this.onEdit,
                            scope: this,
                            iconCls: 'action-icon-custom'
                        },
                        {
                            icon: 'extjs/resources/images/default/dd/drop-no.gif', // Caminho para o ícone de exclusão
                            tooltip: 'Deletar Receita',
                            handler: this.onDelete,
                            scope: this,
                            iconCls: 'action-icon-custom'
                        }
                    ]
                }
            ],
            viewConfig: {
                forceFit: true,
                autoExpandColumn: 'nome_col'
            },
            tbar: [{
                text: 'Nova Receita',
                iconCls: 'x-btn-text-icon-add',
                handler: this.onNew,
                scope: this,
                cls: 'x-btn-over',
                listeners:{
                    'afterrender': function(button) {
                        button.addClass('x-btn-over');
                        button.onMouseOver = Ext.emptyFn;
                        button.onMouseOut = Ext.emptyFn;
                    }
                }
            }],
            bbar: new Ext.PagingToolbar({
                pageSize: 30,
                store: this.store,
                displayInfo: true
            })
        }, config));

        // Listener para redimensionar a grid com a janela
        Ext.EventManager.onWindowResize(this.onWindowResize, this);
    },

    // Handlers para as ações
    onNew: function() {
        new App.view.ReceitaWindow().show();
    },

    onEdit: function(grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);
        new App.view.ReceitaWindow({ record: record }).show();
    },

    onDelete: function(grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);
        Ext.Msg.confirm('Confirmar Exclusão', 'Tem certeza que deseja deletar a receita "' + record.get('nome') + '"?', function(btn) {
            if (btn === 'yes') {
                Ext.Ajax.request({
                    url: 'receitas.jsp?action=deletar',
                    params: { id: record.get('id') },
                    success: function(response) {
                        var result = Ext.decode(response.responseText);
                        if (result.success) {
                            grid.getStore().reload();
                        } else {
                            Ext.Msg.alert('Erro', result.message || 'Falha ao deletar.');
                        }
                    },
                    failure: function(response) {
                        Ext.Msg.alert('Erro', 'Erro de comunicação: ' + response.statusText);
                    }
                });
            }
        });
    },

    onWindowResize: function() {
        var size = Ext.getBody().getViewSize();
        this.setSize(size.width, size.height);
    }
});