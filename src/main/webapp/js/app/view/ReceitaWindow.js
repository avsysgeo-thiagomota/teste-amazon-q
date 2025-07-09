Ext.ns('App.view');

App.view.ReceitaWindow = Ext.extend(Ext.Window, {

    // Construtor da classe
    constructor: function(config) {

        // Stores locais para as grades editáveis
        this.ingredientesStore = new Ext.data.JsonStore({ fields: App.model.Ingrediente });
        this.passosStore = new Ext.data.JsonStore({ fields: App.model.Passo, sortInfo: { field: 'ordem', direction: 'ASC' } });

        // Grade de Ingredientes
        var ingredientesGrid = new Ext.grid.EditorGridPanel({
            store: this.ingredientesStore,
            title: 'Ingredientes',
            height: 160, border: true, clicksToEdit: 1, enableColumnMove: false,
            selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
            columns: [
                {header: 'Nome', dataIndex: 'nome', editor: new Ext.form.TextField({allowBlank: false})},
                {header: 'Qtd.', dataIndex: 'quantidade', width: 60, align: 'right', editor: new Ext.form.NumberField({allowBlank: false, decimalPrecision: 2})},
                {header: 'Unidade', dataIndex: 'unidade', width: 100, editor: new Ext.form.TextField({allowBlank: false})}
            ],
            tbar: [{ text: 'Adicionar', iconCls: 'x-btn-text-icon-add', handler: this.onAddIngrediente, scope: this }, '-', { text: 'Remover', iconCls: 'x-btn-text-icon-delete', handler: this.onRemoveIngrediente, scope: this, grid: ingredientesGrid }],
            viewConfig: { forceFit: true }
        });

        // Grade de Passos
        var passosGrid = new Ext.grid.EditorGridPanel({
            store: this.passosStore,
            title: 'Passos de Preparo',
            height: 160, border: true, clicksToEdit: 1, enableColumnMove: false,
            selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
            columns: [
                {header: 'Ordem', dataIndex: 'ordem', width: 60, sortable: true, editor: new Ext.form.NumberField({allowBlank: false, allowDecimals: false})},
                {header: 'Descrição', dataIndex: 'descricao', editor: new Ext.form.TextField({allowBlank: false})}
            ],
            tbar: [{ text: 'Adicionar', iconCls: 'x-btn-text-icon-add', handler: this.onAddPasso, scope: this }, '-', { text: 'Remover', iconCls: 'x-btn-text-icon-delete', handler: this.onRemovePasso, scope: this, grid: passosGrid }],
            viewConfig: { forceFit: true }
        });

        // Formulário principal
        this.formPanel = new Ext.form.FormPanel({
            frame: true, bodyStyle: 'padding:10px', labelWidth: 100, defaults: { anchor: '98%' },
            items: [
                { xtype: 'hidden', name: 'id' },
                { xtype: 'textfield', fieldLabel: 'Nome da Receita', name: 'nome', allowBlank: false },
                { xtype: 'textarea', fieldLabel: 'Descrição', name: 'descricao', height: 60 },
                { xtype: 'container', layout: 'hbox', defaultType: 'numberfield',
                    items: [
                        { fieldLabel: 'Tempo (min)', name: 'tempoDePreparo', allowBlank: false, width: 200, style: 'margin-right:15px;' },
                        { fieldLabel: 'Porções', name: 'porcoes', allowBlank: false, width: 200, style: 'margin-right:15px;' },
                        { xtype:'textfield', fieldLabel: 'Dificuldade', name: 'dificuldade', allowBlank: false, flex: 1 }
                    ]
                },
                ingredientesGrid,
                passosGrid
            ]
        });

        // Configurações da Janela
        config = Ext.apply({
            title: 'Nova Receita', width: 700, height: 620, layout: 'fit', modal: true,
            items: this.formPanel,
            buttons: [
                { text: 'Salvar', handler: this.onSave, scope: this },
                { text: 'Cancelar', handler: function() { this.close(); }, scope: this }
            ]
        }, config);

        // Chama o construtor da classe pai (Ext.Window)
        App.view.ReceitaWindow.superclass.constructor.call(this, config);

        // Carrega dados se for o modo de edição
        if (this.record) {
            this.setTitle('Editar Receita');
            this.formPanel.getForm().loadRecord(this.record);
            if(this.record.data.ingredientes) this.ingredientesStore.loadData(this.record.get('ingredientes'));
            if(this.record.data.passos) this.passosStore.loadData(this.record.get('passos'));
        }
    },

    // Métodos para manipulação das grades
    onAddIngrediente: function(btn) {
        var grid = btn.findParentByType('editorgrid');
        var Ingrediente = grid.getStore().recordType;
        var p = new Ingrediente({ nome: '', quantidade: 1, unidade: '' });
        grid.stopEditing();
        grid.getStore().add(p);
        grid.getSelectionModel().selectLastRow();
        grid.startEditing(grid.getStore().getCount() - 1, 0);
    },

    onRemoveIngrediente: function(btn) {
        var grid = btn.grid; // Pega a referência da grid passada no handler
        var sm = grid.getSelectionModel();
        if (sm.hasSelection()) grid.getStore().remove(sm.getSelected());
    },

    onAddPasso: function(btn){
        var grid = btn.findParentByType('editorgrid');
        var Passo = grid.getStore().recordType;
        var p = new Passo({ ordem: grid.getStore().getCount() + 1, descricao: '' });
        grid.stopEditing();
        grid.getStore().addSorted(p);
        var idx = grid.getStore().indexOf(p);
        grid.getSelectionModel().selectRow(idx);
        grid.startEditing(idx, 1);
    },

    onRemovePasso: function(btn) {
        var grid = btn.grid;
        var sm = grid.getSelectionModel();
        if (sm.hasSelection()) grid.getStore().remove(sm.getSelected());
    },

    // Método para salvar o formulário
    onSave: function() {
        var form = this.formPanel.getForm();
        if (form.isValid()) {

            // Função auxiliar para extrair dados de um store
            var getStoreData = function(store) {
                var data = [];
                store.each(function(rec) { data.push(rec.data); });
                return data;
            };

            // Coleta dados das grades e os codifica como JSON
            var params = {
                ingredientes: Ext.encode(getStoreData(this.ingredientesStore)),
                passos: Ext.encode(getStoreData(this.passosStore))
            };

            form.submit({
                url: 'receitas.jsp?action=salvar',
                params: params,
                waitMsg: 'Salvando a receita...',
                success: function(form, action) {
                    Ext.Msg.alert('Sucesso', 'Receita salva com sucesso!');
                    App.receitasStore.reload();
                    this.close();
                }.createDelegate(this),
                failure: function(form, action) {
                    var msg = action.result ? action.result.message : 'Erro desconhecido no servidor.';
                    Ext.Msg.alert('Erro', 'Ocorreu um erro ao salvar:<br/>' + msg);
                }
            });
        }
    }
});