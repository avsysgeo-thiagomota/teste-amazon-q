// ReceitaGrid.js

Ext.ns('App.view');

App.view.ReceitaGrid = Ext.extend(Ext.grid.GridPanel, {
    initComponent: function(){
        this.store = new App.store.Receitas({ autoLoad: true });

        Ext.apply(this, {
            title: 'Gerenciador de Receitas',
            store: this.store,
            loadMask: true,
            stripeRows: true,
            columns: [
                {header: 'ID', width: 50, dataIndex: 'id', sortable: true},
                {header: 'Nome', id: 'nome_col', dataIndex: 'nome', sortable: true},
                {header: 'Dificuldade', width: 120, dataIndex: 'dificuldade', sortable: true},
                {header: 'Tempo (min)', width: 120, dataIndex: 'tempoDePreparo', sortable: true, align: 'right'},
                {header: 'Porções', width: 100, dataIndex: 'porcoes', sortable: true, align: 'right'},
                {
                    xtype: 'actioncolumn', width: 50, align: 'center',
                    items: [
                        { icon: 'extjs/resources/images/default/dd/drop-add.gif', tooltip: 'Editar Receita', handler: this.onEdit, scope: this },
                        { xtype: 'spacer', width: 5 },
                        { icon: 'extjs/resources/images/default/dd/drop-no.gif', tooltip: 'Deletar Receita', handler: this.onDelete, scope: this }
                    ]
                }
            ],
            viewConfig: { forceFit: true, autoExpandColumn: 'nome_col' },
            tbar: [{ text: 'Nova Receita', iconCls: 'x-btn-text-icon-add', handler: this.onNew, scope: this }],
            bbar: new Ext.PagingToolbar({
                pageSize: 30, store: this.store, displayInfo: true,
                displayMsg: 'Mostrando receitas {0} - {1} de {2}',
                emptyMsg: "Nenhuma receita para mostrar"
            })
        });

        App.view.ReceitaGrid.superclass.initComponent.call(this);
    },

    // *** PARTE ALTERADA: Lógica de atualização adicionada ***
    onNew: function() {
        var win = new App.view.ReceitaWindow();
        win.on('receitasalva', function() { this.store.reload(); }, this);
        win.show();
    },

    // *** PARTE ALTERADA: Lógica de atualização adicionada ***
    onEdit: function(grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);
        var win = new App.view.ReceitaWindow({ record: record });
        win.on('receitasalva', function() { this.store.reload(); }, this);
        win.show();
    },

    onDelete: function(grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);
        Ext.Msg.confirm('Confirmar Exclusão', 'Tem certeza que deseja deletar a receita "' + record.get('nome') + '"?', function(btn) {
            if (btn === 'yes') {
                grid.loadMask.show();
                Ext.Ajax.request({
                    url: 'receitas.jsp?action=deletar',
                    params: { id: record.get('id') },
                    success: function(response) {
                        grid.loadMask.hide();
                        if (Ext.decode(response.responseText).success) {
                            grid.getStore().reload();
                        } else {
                            Ext.Msg.alert('Erro', 'Falha ao deletar a receita.');
                        }
                    },
                    failure: function() {
                        grid.loadMask.hide();
                        Ext.Msg.alert('Erro', 'Erro de comunicação com o servidor.');
                    }
                });
            }
        });
    }
});