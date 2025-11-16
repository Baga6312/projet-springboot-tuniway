module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  darkMode: 'class', // Add this line
  theme: {
    extend: {
      colors: {
        'tuni-orange': '#FFA726',
        'tuni-teal': '#26C6C9',
        'tuni-dark': '#1A202C',
      }
    },
  },
  plugins: [],
}