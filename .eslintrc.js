module.exports = {
  env: {
    es2021: true,
    'react-native/react-native': true
  },
  extends: ['plugin:react/recommended', 'standard', 'plugin:react-hooks/recommended'],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaFeatures: {
      jsx: true
    },
    ecmaVersion: 12,
    sourceType: 'module'
  },
  plugins: ['react', 'react-native', '@typescript-eslint', 'react-hooks'],
  overrides: [],
  rules: {
    eqeqeq: ['warn', 'smart'],
    'react/prop-types': 'off',
    'space-before-function-paren': ['error', { anonymous: 'always', named: 'never', asyncArrow: 'always' }],
    'multiline-ternary': ['off'],
    'no-unused-vars': 'off',
    '@typescript-eslint/no-unused-vars': ['error', { args: 'none' }],
    'no-tabs': 'off',
    semi: 'off',
    'comma-dangle': 'off',
    'object-curly-spacing': 'off',
    'spaced-comment': 'off',
    'react-hooks/rules-of-hooks': 'error',
    'react/display-name': 'off',
    'react-hooks/exhaustive-deps': 'warn',
    '@typescript-eslint/no-explicit-any': 'warn',
    'n/no-callback-literal': 0,
  },
  globals: {
    gStr: true,
    gScreenW: true,
    gScreenH: true,
    gIsIOS: true,
    gToast: true,
    gIsWeb: true,
    showLoading: true,
    hideLoading: true,
    getLoadingText: true,
    $t: true
  }
}
