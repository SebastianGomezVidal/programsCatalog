/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';

window.$departure = ''; //global variable
window.$arrival = ''; //global variable

AppRegistry.registerComponent(appName, () => App);
