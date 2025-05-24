require "nvchad.mappings"

-- add yours here

local map = vim.keymap.set

map("n", ";", ":", { desc = "CMD enter command mode" })
map("i", "jk", "<ESC>")

-- map({ "n", "i", "v" }, "<C-s>", "<cmd> w <cr>")

map({ "n", "t" }, "<A-i>", function()
  require("nvchad.term").toggle {
    pos = "float",
    id = "floatTerm",
    float_opts = {
      row = 0.05,
      col = 0.05,
      width = 0.9,
      height = 0.8,
    },
  }
end, { desc = "terminal toggle floating term" })

map("n", "<Space>q", ":%bd|e#<cr>", { desc = "Close all buffers except current" })
map("n", "<C-q>", ":qa!<cr>", { desc = "quit nvchad" })
map("n", "<Space>tk", ":Telescope keymaps<cr>", { desc = "Telescope keymaps" })
map("n", "H", ":bp<cr>", { desc = "previous buffer" })
map("n", "L", ":bn<cr>", { desc = "next buffer" })
map("n", "-", "10<C-w><<cr>", { desc = "desc width"})
map("n", "=", "10<C-w>><cr>", { desc = "desc width"})

-- Trouble
map("n", "<Space>td", ":Trouble diagnostics toggle<cr>", { desc = "Trouble diagnostics"})
map("n", "<Space>ts", ":Trouble symbols toggle<cr>", { desc = "Trouble symbols"})
map("n", "<Space>tq", ":Trouble qflist toggle<cr>", { desc = "Trouble quickfix"})
map("n", "<Space>tl", ":Trouble lsp toggle<cr>", { desc = "Trouble lsp"})
map("n", "<Space>tf", ":Trouble loclist toggle<cr>", { desc = "Trouble loclist"})
