require "nvchad.options"

-- add yours here!

-- local o = vim.o
-- o.cursorlineopt ='both' -- to enable cursorline!

local cmp = require('cmp')

cmp.setup({
  sources = cmp.config.sources({
    { name = 'nvim_lsp' },
    { name = 'vim-dadbod-completion' }, -- Add this
    { name = 'buffer' },
    -- other sources
  }),
})

-- Optional: Only enable dadbod completion for SQL files
vim.g.completion_chain_complete_list = {
  sql = {
    { complete_items = { 'vim-dadbod-completion' } },
  },
}

require('telescope').setup({
  defaults = {
    layout_strategy = 'vertical',
    layout_config = {
      prompt_position = 'top',
      preview_cutoff = 0,
      height = 0.95,
      preview_height = 0.6,
    },
    sorting_strategy = 'ascending',
  },
})
