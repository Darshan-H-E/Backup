require("nvchad.configs.lspconfig").defaults()

local servers = { "pyright", "gopls", "sqls", "typescript_language_server", "ts_ls"}
vim.lsp.enable(servers)

-- read :h vim.lsp.config for changing options of lsp servers 
