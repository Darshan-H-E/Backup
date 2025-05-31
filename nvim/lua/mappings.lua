require "nvchad.mappings"

local map = vim.keymap.set

map("n", ";", ":", { desc = "CMD enter command mode" })
map("i", "jk", "<ESC>")

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
map("n", "-", "10<C-w><<cr>", { desc = "desc width" })
map("n", "=", "10<C-w>><cr>", { desc = "desc width" })

-- Trouble
map("n", "<Space>td", ":Trouble diagnostics toggle<cr>", { desc = "Trouble diagnostics" })
map("n", "<Space>ts", ":Trouble symbols toggle<cr>", { desc = "Trouble symbols" })
map("n", "<Space>tq", ":Trouble qflist toggle<cr>", { desc = "Trouble quickfix" })
map("n", "<Space>tl", ":Trouble lsp toggle<cr>", { desc = "Trouble lsp" })
map("n", "<Space>tf", ":Trouble loclist toggle<cr>", { desc = "Trouble loclist" })

-- Git signs
map("n", "<Space>gb", ":Gitsigns blame<cr>", { desc = "Blame" })
map("n", "<Space>gd", ":Gitsigns toggle_deleted<cr>", { desc = "Deleted" })
map("n", "<Space>gp", ":Gitsigns preview_hunk_inline<cr>", { desc = "Preview" })

-- DBUI
map("n", "<Space>do", ":DBUIToggle<cr>", { desc = "DBUI" })

-- Spectre
map({ "n" }, "<leader>sr", function()
  require("spectre").toggle()
end, { desc = "Toggle spectre" })

map({ "n" }, "<leader>sw", function()
  require("spectre").open_visual { select_word = true }
end, { desc = "Search current word" })

map({ "n" }, "<leader>sf", function()
  require("spectre").open_file_search()
end, { desc = "Search in current file" })
