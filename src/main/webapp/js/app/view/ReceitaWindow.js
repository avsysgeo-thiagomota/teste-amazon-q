Ext.ns('App.view');

App.view.ReceitaWindow = Ext.extend(Ext.Window, {
    title: 'Nova Receita',
    width: 700,
    height:'auto',
    modal: true,
    initComponent: function() {
        this.ingredientesStore = new Ext.data.JsonStore({ fields: App.model.Ingrediente });
        this.passosStore = new Ext.data.JsonStore({ fields: App.model.Passo, sortInfo: { field: 'ordem', direction: 'ASC' } });

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
            tbar: [{ text: 'Adicionar', iconCls: 'x-btn-text-icon-add', handler: this.onAddIngrediente, scope: this }, '-', { text: 'Remover', iconCls: 'x-btn-text-icon-delete', handler: this.onRemoveIngrediente, scope: this }],
            viewConfig: { forceFit: true }
        });

        var passosGrid = new Ext.grid.EditorGridPanel({
            store: this.passosStore,
            title: 'Passos de Preparo',
            height: 160, border: true, clicksToEdit: 1, enableColumnMove: false,
            selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
            columns: [
                {header: 'Ordem', dataIndex: 'ordem', width: 60, sortable: true, editor: new Ext.form.NumberField({allowBlank: false, allowDecimals: false})},
                {header: 'Descrição', dataIndex: 'descricao', editor: new Ext.form.TextField({allowBlank: false})}
            ],
            tbar: [{ text: 'Adicionar', iconCls: 'x-btn-text-icon-add', handler: this.onAddPasso, scope: this }, '-', { text: 'Remover', iconCls: 'x-btn-text-icon-delete', handler: this.onRemovePasso, scope: this }],
            viewConfig: { forceFit: true }
        });

        this.formPanel = new Ext.form.FormPanel({
            frame: true, bodyStyle: 'padding:10px', labelWidth: 80,
            defaults: { anchor: '98%' },
            items: [
                { xtype: 'hidden', name: 'id' },
                { xtype: 'textfield', fieldLabel: 'Nome', name: 'nome', allowBlank: false },
                { xtype: 'textarea', fieldLabel: 'Descrição', name: 'descricao', height: 60 },
                {
                    xtype: 'container', layout:'column', border: false,
                    defaults: { columnWidth: .33, layout: 'form', border: false },
                    items: [
                        { items: { xtype: 'numberfield', fieldLabel: 'Tempo (min)', name: 'tempoDePreparo', anchor:'95%' } },
                        { items: { xtype: 'numberfield', fieldLabel: 'Porções', name: 'porcoes', anchor:'95%' } },
                        { items: { xtype: 'textfield', fieldLabel: 'Dificuldade', name: 'dificuldade', anchor:'95%' } }
                    ]
                },
                ingredientesGrid,
                passosGrid
            ]
        });

        Ext.apply(this, {
            items: this.formPanel,
            buttons: [
                { text: 'Salvar', handler: this.onSave, scope: this },
                { text: 'Cancelar', handler: function() { this.close(); }, scope: this }
            ]
        });

        this.addEvents('receitasalva');
        App.view.ReceitaWindow.superclass.initComponent.call(this);

        if (this.record) {
            this.setTitle('Editar Receita: ' + this.record.get('nome'));
            this.formPanel.getForm().loadRecord(this.record);

            // Garante que as stores estejam vazias antes de carregar para evitar duplicação
            this.ingredientesStore.removeAll();
            this.passosStore.removeAll();

            var ingredientesData = this.record.get('ingredientes');
            if (ingredientesData) {
                this.ingredientesStore.loadData(ingredientesData);
            }

            var passosData = this.record.get('passos');
            if (passosData) {
                this.passosStore.loadData(passosData);
            }
        }
    },

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
        var grid = btn.findParentByType('editorgrid');
        var record = grid.getSelectionModel().getSelected();
        if (record) { grid.getStore().remove(record); }
        else { Ext.Msg.alert('Atenção', 'Selecione um ingrediente para remover.'); }
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
        var grid = btn.findParentByType('editorgrid');
        var record = grid.getSelectionModel().getSelected();
        if (record) { grid.getStore().remove(record); }
        else { Ext.Msg.alert('Atenção', 'Selecione um passo para remover.'); }
    },

    onSave: function() {
        var form = this.formPanel.getForm();
        if (form.isValid()) {
            var getStoreData = function(store) {
                return store.getRange().map(function(rec) { return rec.data; });
            };
            var formValues = form.getValues();
            if (formValues.id === "") {
                delete formValues.id;
            }
            formValues.ingredientes = getStoreData(this.ingredientesStore);
            formValues.passos = getStoreData(this.passosStore);

            form.submit({
                url: 'receitas',
                params: { jsonData: Ext.encode(formValues) },
                waitMsg: 'Salvando a receita...',
                success: function(form, action){
                    Ext.MessageBox.alert('Sucesso', 'Receita salva com sucesso!', (form, action)=>{
                        this.fireEvent('receitasalva', this, action.result);
                        this.close();
                    })
                }.createDelegate(this),
                failure: function(form, action) {
                    Ext.Msg.alert('Erro', 'Ocorreu um erro ao salvar:<br/>' + (action.result ? action.result.message : 'Erro desconhecido.'));
                }
            });
        }
    }
});