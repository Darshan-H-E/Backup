 local function map(mode, lhs, rhs, opts)
    local options = {noremap = true}
    if opts then
        options = vim.tbl_extend("force", options, opts)
    end
    vim.api.nvim_set_keymap(mode, lhs, rhs, options)
end

local opt = {}

-- dont copy any deleted text , this is disabled by default so uncomment the below mappings if you want them!
--[[ remove this line

map("n", "dd", [=[ "_dd ]=], opt)
map("v", "dd", [=[ "_dd ]=], opt)
map("v", "x", [=[ "_x ]=], opt)

 this line too ]]
-- OPEN TERMINALS --
map("n", "<leader>t", [[<Cmd>vnew term://bash <CR>]], opt) -- term over right
map("n", "<leader>n", [[<Cmd> split term://bash | resize 10 <CR>]], opt) --  term bottom
map("n", "<C-t>t", [[<Cmd> tabnew | term <CR>]], opt) -- term newtab

-- COPY EVERYTHING --
map("n", "<C-a>", [[ <Cmd> %y+<CR>]], opt)

map('i', 'jk', [[<ESC>]], opt)
map("n", "s", [[<Cmd>w<CR>]], opt)
map("n", "<leader>/",":Commentary<CR>")
map("v", "<leader>/",":Commentary<CR>")
map("n", "<leader>h",":noh<CR>")

-- better indenting
map('v', '<', '<gv', {noremap = true, silent = true})
map('v', '>', '>gv', {noremap = true, silent = true})

-- better window movement
map('n', '<C-h>', '<C-w>h', {silent = true})
map('n', '<C-j>', '<C-w>j', {silent = true})
map('n', '<C-k>', '<C-w>k', {silent = true})
map('n', '<C-l>', '<C-w>l', {silent = true})

-- maximize width of split
map('n', '<leader>[', '<C-w>|', {silent = true})
-- equalize width and height of split
map('n', '<leader>]', '<C-w>=', {silent = true})

-- switch buffer
map('n', '<S-l>', ':bnext<CR>', {noremap = true, silent = true})
map('n', '<S-h>', ':bprevious<CR>', {noremap = true, silent = true})
map('n', '<S-f>', ':bd<CR>', {noremap = true, silent = true})

map('x', 'K', ':move \'<-2<CR>gv-gv', {noremap = true, silent = true})
map('x', 'J', ':move \'>+1<CR>gv-gv', {noremap = true, silent = true})

-- resize with arrows
vim.cmd([[
  nnoremap <silent> <C-Up>    :resize -2<CR>
  nnoremap <silent> <C-Down>  :resize +2<CR>
  nnoremap <silent> <C-[> :vertical resize -2<CR>
  nnoremap <silent> <C-]> :vertical resize +2<CR>
]])

vim.cmd([[
  tnoremap <C-h> <C-\><C-N><C-w>h
  tnoremap <C-j> <C-\><C-N><C-w>j
  tnoremap <C-k> <C-\><C-N><C-w>k
  inoremap <C-h> <C-\><C-N><C-w>h
  inoremap <C-j> <C-\><C-N><C-w>j
  inoremap <C-k> <C-\><C-N><C-w>k
  tnoremap <Esc> <C-\><C-n>
]])

-- execute
map('n','<leader>rh',':w<CR>:ter gcc -g % -o ./bin/%:r && ./bin/%:r <CR>', {noremap = true})
map('n','<leader>rj',':w<CR>:ter g++ -g % -o ./bin/%:r && ./bin/%:r <CR>', {noremap = true})
-- map('n','<C-n>',':vs<CR>:ter python3 "%"<CR>', {noremap = true})
map('n','<leader>rk',':w<CR>:ter python3 "%"<CR>', {noremap = true})
map('n','<leader>rl',':w<CR>:ter node "%"<CR>', {noremap = true})


