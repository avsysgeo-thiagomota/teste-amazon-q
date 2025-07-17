Ext.ns('App.view');

// Inicia as validações customizadas ANTES de definir a janela
Ext.apply(Ext.form.VTypes, {
    // Validação para confirmar se as duas senhas são iguais
    password: function(val, field) {
        if (field.initialPassField) {
            var pwd = Ext.getCmp(field.initialPassField);
            return (val == pwd.getValue());
        }
        return true;
    },
    passwordText: 'As senhas não conferem.'
});


App.view.UsuarioWindow = Ext.extend(Ext.Window, {
    title: 'Cadastro de Novo Usuário',
    width: 400,
    height: 250,
    layout: 'fit',
    modal: true,

    initComponent: function() {
        this.formPanel = new Ext.form.FormPanel({
            frame: true,
            bodyStyle: 'padding:10px',
            labelWidth: 110,
            defaults: { anchor: '95%' },
            items: [
                { xtype: 'textfield', fieldLabel: 'Nome Completo', name: 'nome_completo', allowBlank: false },
                { xtype: 'textfield', fieldLabel: 'E-mail', name: 'email', allowBlank: false, vtype: 'email' },
                { xtype: 'textfield', fieldLabel: 'Nome de Usuário', name: 'username', allowBlank: false },
                { xtype: 'textfield', fieldLabel: 'Senha', name: 'senha', inputType: 'password', allowBlank: false, id: 'pass' },
                { xtype: 'textfield', fieldLabel: 'Confirmar Senha', name: 'confirmar_senha', inputType: 'password', allowBlank: false, vtype: 'password', initialPassField: 'pass' }
            ]
        });

        Ext.apply(this, {
            items: this.formPanel,
            buttons: [
                { text: 'Salvar', handler: this.onSave, scope: this },
                { text: 'Cancelar', handler: function() { this.close(); }, scope: this }
            ]
        });

        App.view.UsuarioWindow.superclass.initComponent.call(this);
    },

    onSave: function() {
        var form = this.formPanel.getForm();
        if (form.isValid()) {
            var formValues = form.getValues();

            Ext.Ajax.request({
                url: 'usuarios', // Aponta para o novo UsuarioServlet
                method: 'POST',
                jsonData: formValues,
                waitMsg: 'Salvando...',
                scope: this,
                success: function(response, options) {
                    var result = Ext.decode(response.responseText);
                    Ext.Msg.alert('Sucesso', result.message);
                    this.close();
                },
                failure: function(response, options) {
                    var result = Ext.decode(response.responseText);
                    Ext.Msg.alert('Erro', result.message || 'Ocorreu um erro no cadastro.');
                }
            });
        }
    }
});