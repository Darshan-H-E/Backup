local function map(mode, lhs, rhs, opts)
    local options = {noremap = true}
    if opts then
        options = vim.tbl_extend("force", options, opts)
    end
    vim.api.nvim_set_keymap(mode, lhs, rhs, options)
end

-- copy any selected text with pressing y
map("", "<leader>c", '"+y')
map("n", "<leader>/",":Commentary<CR>")
map("n", "s",":w<CR>")

-- OPEN TERMINALS --
map("n", "<leader>t", [[<Cmd>vnew term://bash <CR>]], opt) -- open term over right
-- map("n", "<leader>n", [[<Cmd> split term://bash | resize 10 <CR>]], opt) -- open term bottom
map("n", "<leader>n",": ter <CR>i" , opt) -- open term bottom

-- COPY EVERYTHING --
-- map("n", "<C-a>", [[ <Cmd> %y+<CR>]], opt)

-- better window movement
map('n', '<C-h>', '<C-w>h', {silent = true})
map('n', '<C-j>', '<C-w>j', {silent = true})
map('n', '<C-k>', '<C-w>k', {silent = true})
map('n', '<C-l>', '<C-w>l', {silent = true})
--vim.api.nvim_set_keymap('n', '<C-l>', '<C-w>l', {silent = true})


-- TODO fix this
-- Terminal window navigation
-- tnoremap <C-l> <C-\><C-N><C-w>l
-- inoremap <C-l> <C-\><C-N><C-w>l
vim.cmd([[
  tnoremap <C-h> <C-\><C-N><C-w>h
  tnoremap <C-j> <C-\><C-N><C-w>j
  tnoremap <C-k> <C-\><C-N><C-w>k
  inoremap <C-h> <C-\><C-N><C-w>h
  inoremap <C-j> <C-\><C-N><C-w>j
  inoremap <C-k> <C-\><C-N><C-w>k
  tnoremap <Esc> <C-\><C-n>
]])

-- TODO fix this
-- resize with arrows
vim.cmd([[
  nnoremap <silent> <C-Up>    :resize -2<CR>
  nnoremap <silent> <C-Down>  :resize +2<CR>
  nnoremap <silent> <C-[> :vertical resize -2<CR>
  nnoremap <silent> <C-]> :vertical resize +2<CR>
]])

-- "Clang
-- autocmd BufNewFile *.cpp execute "0r ~/.vim/template/".input("Template name: ").".cpp"
-- map <C-x> <esc>:w<CR><esc>:vsplit<CR>:vert ter g++ -g % -o bin/%:r && ./bin/%:r <CR>

map('n','<leader>lj',':w<CR>:vs<CR>:ter ./%:r.sh<CR>', {noremap = true})
-- map('n','<leader>ll',':ter g++ -g % -o bin/%:r && ./bin/%:r <CR>', {noremap = true})
map('n','<leader>ll',':ter g++ -g % -o %:r && ./%:r <CR>', {noremap = true})
-- map('n','<C-n>',':vs<CR>:ter python3 "%"<CR>', {noremap = true})
map('n','<leader>lk',':ter python3 "%"<CR>', {noremap = true})
-- better indenting
map('v', '<', '<gv', {noremap = true, silent = true})
map('v', '>', '>gv', {noremap = true, silent = true})

-- I hate escape
map('i', 'jk', '<ESC>', {noremap = true, silent = true})

-- Tab switch buffer
map('n', '<S-l>', ':bnext<CR>', {noremap = true, silent = true})
map('n', '<S-h>', ':bprevious<CR>', {noremap = true, silent = true})

-- Move selected line / block of text in visual mode
map('x', 'K', ':move \'<-2<CR>gv-gv', {noremap = true, silent = true})
map('x', 'J', ':move \'>+1<CR>gv-gv', {noremap = true, silent = true})
