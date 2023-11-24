# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).





# Getting Started with installing Tailwind

## Available Scripts

Install Tailwind and its peer-dependencies using npm:
### `npm install -D tailwindcss@npm:@tailwindcss/postcss7-compat postcss@^7 autoprefixer@^9`

Since Create React App doesn’t let you override the PostCSS configuration natively, we also need to install [CRACO](https://github.com/dilanx/craco) to be able to configure Tailwind:
### `npm install @craco/craco`

Once it’s installed, update your `scripts` in your `package.json` file to use craco instead of `react-scripts` for all scripts except `eject`:
    
     "start": "craco start",
     "build": "craco build",
     "test":  "craco test"

Next, create a `craco.config.js` at the root of our project and add the `tailwindcss` and `autoprefixer` as PostCSS plugins:

    // craco.config.js
    module.exports = {
    style: {
    postcss: {
      plugins: [
        require('tailwindcss'),
        require('autoprefixer'),
               ],
             },
           },
    }

Next, generate your `tailwind.config.js` file:

### `npx tailwindcss-cli@latest init`

This will create a minimal `tailwind.config.js` file at the root of your project:

        // tailwind.config.js
        module.exports = {
        purge: [],
        darkMode: false, // or 'media' or 'class'
        theme: {
            extend: {},
        },
        variants: {
            extend: {},
        },
        plugins: [],
        }

## Learn More
You can learn more about configuring Tailwind in the [configuration documentation](https://v2.tailwindcss.com/docs/configuration).


# Include Tailwind in your CSS
Open the `./src/index.css` file that Create React App generates for you by default and use the `@tailwind` directive to include Tailwind’s `base`, `components`, and `utilities` styles, replacing the original file contents:

        /* ./src/index.css */
        @tailwind base;
        @tailwind components;
        @tailwind utilities;

Read documentation on [adding base styles](https://v2.tailwindcss.com/docs/adding-base-styles), [extracting components](https://v2.tailwindcss.com/docs/extracting-components), and [adding new utilities](https://v2.tailwindcss.com/docs/adding-new-utilities) for best practices on extending Tailwind with your own custom CSS.

Finally, ensure your CSS file is being imported in your `./src/index.js` file:

    import './index.css';

You’re finished! Now when you run `npm run start`, Tailwind CSS will be ready to use in your Create React App project.