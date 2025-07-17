Ext.ns('App.view');

App.view.ConfiguracoesWindow = Ext.extend(Ext.Window, {
    title: 'Configurações da Conta',
    width: 400,
    height:'auto',
    layout: 'form',
    modal: true,
    bodyStyle: 'padding:15px;',

    initComponent: function() {
        Ext.apply(this, {
            items: [{
                xtype: 'label',
                text: 'Atenção: A exclusão da sua conta é uma ação permanente e não pode ser desfeita.'
            }],
            buttons: [{
                text: 'Excluir Minha Conta Permanentemente',
                iconCls: 'x-icon-delete',
                handler: this.onDeleteAccount,
                scope: this
            }, {
                text: 'Cancelar',
                handler: function() { this.close(); },
                scope: this
            }]
        });
        App.view.ConfiguracoesWindow.superclass.initComponent.call(this);
    },

    onDeleteAccount: function() {
        Ext.Msg.confirm('Confirmação Final', 'Você tem certeza ABSOLUTA que deseja excluir sua conta? Todos os seus dados serão perdidos.', function(btn) {
            if (btn === 'yes') {
                this.el.mask('Excluindo...', 'x-mask-loading');
                Ext.Ajax.request({
                    url: 'usuarios', // Aponta para o doDelete do UsuarioServlet
                    method: 'DELETE',
                    scope: this,
                    success: function(response) {
                        this.el.unmask();
                        Ext.Msg.alert('Conta Excluída', 'Sua conta foi removida com sucesso.', function() {
                            window.location = 'login.jsp';
                        });
                    },
                    failure: function(response) {
                        this.el.unmask();
                        var result = Ext.decode(response.responseText);
                        Ext.Msg.alert('Erro', result.message || 'Não foi possível excluir a conta.');
                    }
                });
            }
        }, this);
    }
});