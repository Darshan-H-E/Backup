require "nvchad.mappings"

local map = vim.keymap.set

-----------------------------------------------------------------------
-- Essentials
-----------------------------------------------------------------------
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
map("n", "<Space>;", ":bd!<cr>", { desc = "force quit buffer" })
map("n", "<Space>tk", ":Telescope keymaps<cr>", { desc = "Telescope keymaps" })
map("n", "H", ":bp<cr>", { desc = "previous buffer" })
map("n", "L", ":bn<cr>", { desc = "next buffer" })
map("n", "-", "10<C-w><<cr>", { desc = "dec width" })
map("n", "=", "10<C-w>><cr>", { desc = "inc width" })
map("n", "_", "10<C-w>-<cr>", { desc = "dec height" })
map("n", "+", "10<C-w>+<cr>", { desc = "inc height" })

map("n", "E", function()
  vim.diagnostic.open_float(nil, { focusable = false })
end, { desc = "Show diagnostics under cursor" })

-----------------------------------------------------------------------
-- Trouble
-----------------------------------------------------------------------
map("n", "<Space>td", ":Trouble diagnostics toggle<cr>", { desc = "Trouble diagnostics" })
map("n", "<Space>ts", ":Trouble symbols toggle<cr>", { desc = "Trouble symbols" })
map("n", "<Space>tq", ":Trouble qflist toggle<cr>", { desc = "Trouble quickfix" })
map("n", "<Space>tl", ":Trouble lsp toggle<cr>", { desc = "Trouble lsp" })
map("n", "<Space>tf", ":Trouble loclist toggle<cr>", { desc = "Trouble loclist" })

-----------------------------------------------------------------------
-- Git signs
-----------------------------------------------------------------------
map("n", "<Space>gb", ":Gitsigns blame<cr>", { desc = "Blame" })
map("n", "<Space>gd", ":Gitsigns toggle_deleted<cr>", { desc = "Deleted" })
map("n", "<Space>gp", ":Gitsigns preview_hunk_inline<cr>", { desc = "Preview" })

-----------------------------------------------------------------------
-- DBUI
-----------------------------------------------------------------------
map("n", "<Space>do", ":DBUIToggle<cr>", { desc = "DBUI" })

-----------------------------------------------------------------------
-- Spectre
-----------------------------------------------------------------------
map("n", "<leader>sr", function()
  require("spectre").toggle()
end, { desc = "Toggle spectre" })

map("n", "<leader>sw", function()
  require("spectre").open_visual { select_word = true }
end, { desc = "Search current word" })

map("n", "<leader>sf", function()
  require("spectre").open_file_search()
end, { desc = "Search in current file" })

-----------------------------------------------------------------------
-- Run File
-----------------------------------------------------------------------
local function run_file(cmd)
  vim.cmd("w")
  vim.cmd("split | terminal " .. cmd .. " " .. vim.fn.expand("%"))
end

map("n", "<leader>op", function()
  run_file("python3")
end, { desc = "Run Python file" })

map("n", "<leader>og", function()
  run_file("go run")
end, { desc = "Run Go file" })

-----------------------------------------------------------------------
-- Projectwide compile and run
-----------------------------------------------------------------------
local term_bufnr = nil  -- Store terminal buffer number
local term_winid = nil  -- Store window ID

local function toggle_terminal(command)
  if term_winid and vim.api.nvim_win_is_valid(term_winid) then
    vim.api.nvim_win_hide(term_winid)
    term_winid = nil
  elseif term_bufnr and vim.api.nvim_buf_is_valid(term_bufnr) then
    vim.cmd("split")
    vim.api.nvim_win_set_buf(0, term_bufnr)
    term_winid = vim.api.nvim_get_current_win()
  else
    vim.cmd("split")
    vim.cmd("terminal " .. command)
    term_bufnr = vim.api.nvim_get_current_buf()
    term_winid = vim.api.nvim_get_current_win()
  end
end

map("n", "<leader>hr", function()
  toggle_terminal("npm start")
end, { desc = "Toggle terminal split for dev server" })

-- Reflex
-- # Install reflex (if not installed)
-- go install github.com/cespare/reflex@latest
map("n", "<leader>hg", function()
  toggle_terminal("reflex -r '\\.go$' -- sh -c 'clear && go run .'")
end, { desc = "Toggle Go project runner with auto rebuild" })
