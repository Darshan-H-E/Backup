-- This file needs to have same structure as nvconfig.lua
-- https://github.com/NvChad/ui/blob/v3.0/lua/nvconfig.lua
-- Please read that file to know all available options :(

---@type ChadrcConfig
local M = {}

M.base46 = {
  changed_themes = {
    nord = {
      -- base_16 = {
      --   base0A = "#83c092",
      --   base0B = "#7b9b98",
      --   base0D = "#e67e80",
      -- },
      base_16 = {
        base0A = "#A3BE8C",
        base0B = "#7b9b98",
        base0D = "#BF616A",
      },
    },
  },

  hl_override = {
    -- DiagnosticError = { fg = "#e7c6ff" },
    DiagnosticError = { fg = "#f56a00" },
  },
  theme = "nord",
  transparency = true,
}

return M
