/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {NavigationContainer, DefaultTheme} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import SearchScreen from './SearchRoute';
import RouteResultsScreen from './RouteResultsScreen';
import RouteDetailsScreen from './RouteDetails';


const Stack = createStackNavigator();

const pageOneName = 'searchRoute';
const pageTwoName = 'routeResults';
const pageThreeName = 'routeDetails';

const MyTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    card: 'rgb(0,77,64)',
    text: 'rgb(236,239,241)',
  },
};

const App = () => {
  return (
    <NavigationContainer theme={MyTheme}>
      <Stack.Navigator initialRouteName={pageOneName}>
        <Stack.Screen
          name={pageOneName}
          component={SearchScreen}
          options={{title: 'Find your route'}}
        />
        <Stack.Screen name={pageTwoName} component={RouteResultsScreen} />
        <Stack.Screen name={pageThreeName} component={RouteDetailsScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

/*const RouteDetailsScreen = (props) => {

    const chosenConnection = props.route.params.connection

    console.log("Item received : " + JSON.stringify(chosenConnection.sections))



    return <Text>RouteDetails</Text>
}*/

export default App;
