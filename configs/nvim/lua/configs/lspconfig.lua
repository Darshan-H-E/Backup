require("nvchad.configs.lspconfig").defaults()

local servers = { "gopls", "sqls", "ts_ls"}
vim.lsp.enable(servers)
