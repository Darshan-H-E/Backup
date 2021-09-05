-- check if packer is installed (~/local/share/nvim/site/pack)
local packer_exists = pcall(vim.cmd, [[packadd packer.nvim]])

-- using { } when using a different branch of the plugin or loading the plugin with certain commands
return require("packer").startup(
    function()
        use {"wbthomason/packer.nvim", opt = true}
        use {"lukas-reineke/indent-blankline.nvim", branch = "lua"}
        -- use 'hrsh7th/vim-vsnip'
        -- use 'hrsh7th/vim-vsnip-integ'
        -- use 'Neevash/awesome-flutter-snippets'
        use "norcalli/nvim-base16.lua"
        use "kyazdani42/nvim-web-devicons"
        use "kyazdani42/nvim-tree.lua"
        use "nvim-lua/plenary.nvim"
        use "lewis6991/gitsigns.nvim"
        use "akinsho/nvim-bufferline.lua"
        -- use "glepnir/galaxyline.nvim"
        use {
        'hoob3rt/lualine.nvim',
        requires = {'kyazdani42/nvim-web-devicons', opt = true}
        }
        -- use "907th/vim-auto-save"
        use "nvim-treesitter/nvim-treesitter"
        use "norcalli/nvim-colorizer.lua"
        use "ryanoasis/vim-devicons"
        -- use "sbdchd/neoformat"
        use "neovim/nvim-lspconfig"
        use "hrsh7th/nvim-compe"
        use "windwp/nvim-autopairs"
        use "alvan/vim-closetag"
        use "tweekmonster/startuptime.vim"
        use "onsails/lspkind-nvim"
        use "nvim-telescope/telescope.nvim"
        use "nvim-telescope/telescope-media-files.nvim"
        use "nvim-lua/popup.nvim"
        use 'tpope/vim-commentary'

        -- typescript
        use 'windwp/nvim-ts-autotag'
        use 'mattn/emmet-vim'
        -- use 'neoclide/vim-jsx-improve'
        use 'MaxMEllon/vim-jsx-pretty'

        -- use "vimwiki/vimwiki"

        --flutter
        -- use {'akinsho/flutter-tools.nvim', requires = 'nvim-lua/plenary.nvim'}

        --colorschemes
        use 'bluz71/vim-nightfly-guicolors'
    end
)
