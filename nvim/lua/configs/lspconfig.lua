require("nvchad.configs.lspconfig").defaults()

local servers = { "gopls", "sqls", "ts_ls"}
vim.lsp.enable(servers)

-- read :h vim.lsp.config for changing options of lsp servers 
