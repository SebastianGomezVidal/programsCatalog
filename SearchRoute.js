import React from 'react';
import {
  StyleSheet,
  TextInput,
  Text,
  View,
  Button,
  Alert,
  Image,
  SafeAreaView,
  ScrollView,
} from 'react-native';
import {Keyboard} from 'react-native';
import RNDateTimePicker from '@react-native-community/datetimepicker';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import Autocomplete from 'react-native-autocomplete-input';
import {YellowBox} from 'react-native';

YellowBox.ignoreWarnings([
  'VirtualizedLists should never be nested', // TODO: Remove when fixed
]);

//Beware! All the parameter setted inside the JSX element are put inside a props object that is the only parameter
//passed to the function
const CustomTextInput = ({
  title,
  onChangeText,
  placeholder = 'Lausanne',
  stationList,
}) => {
  //Alert.alert(initialText.toString())
  //Alert.alert(style.toString())

  //const [value, onChangeText] = React.useState(initialText);

  return (
    <View>
      <Text style={styles.titleStyle}>{title}</Text>
      <TextInput
        style={styles.textInputStyle}
        placeholder={placeholder}
        onChangeText={text => {
          onChangeText(text);
        }}
        //value={value}
      />
    </View>
  );
};

function pad(s) {
  return s < 10 ? '0' + s : s;
}

function extractDate(date) {
  return [
    pad(date.getDate()),
    pad(date.getMonth() + 1),
    date.getFullYear(),
  ].join('/');
}

function extractDateForUrl(date) {
  return [
    date.getFullYear(),
    pad(date.getMonth() + 1),
    pad(date.getDate()),
  ].join('-');
}

function extractTime(date) {
  return [pad(date.getHours()), pad(date.getMinutes())].join(':');
}

const DateTimeInput = ({departureDateTime, onChangeDepartureDateTime}) => {
  const [mode, setMode] = React.useState('date'); //it allow to decide if showing date or time picker
  const [show, setShow] = React.useState(false); //it tells if the time or datepicker is shown or not
  const [optional, setOptional] = React.useState(true); // it tells if the date and time are optional
  // or not for the user. It is true if the user
  // has not inserted date or time and it is false
  // otherwise

  const onChange = (event, selectedDate) => {
    //"currentDate" is set to "selectedDate" except the case in which is undefined.
    //In that case, "currentDate" is set to "departureDateTime"
    const currentDate = selectedDate || departureDateTime;
    console.log('Selected date : ' + selectedDate);
    console.log('Date : ' + departureDateTime);
    console.log('Current date : ' + currentDate);
    setShow(Platform.OS === 'ios');
    onChangeDepartureDateTime(currentDate);
    setOptional(false);
  };

  const showMode = currentMode => {
    setShow(true);
    setMode(currentMode);
  };

  const showDatepicker = () => {
    Keyboard.dismiss();
    showMode('date');
  };

  const showTimepicker = () => {
    Keyboard.dismiss();
    showMode('time');
  };

  return (
    <View style={styles.dateAndTimeViewStyle}>
      <View style={{...styles.dateOrTimeViewStyle, marginEnd: 16}}>
        <Text style={styles.titleStyle}>Date</Text>
        <TextInput
          style={styles.textInputStyle}
          placeholder={extractDate(new Date())}
          onFocus={showDatepicker}
          value={
            optional === true ? '' : extractDate(departureDateTime) // if optional === true, shows an empty field
          }
          //onChangeText={text => onChangeDepartureDate(text)}
        />
      </View>
      <View style={styles.dateOrTimeViewStyle}>
        <Text style={styles.titleStyle}>Time</Text>
        <TextInput
          style={styles.textInputStyle}
          placeholder={extractTime(new Date())}
          onFocus={showTimepicker}
          value={optional === true ? '' : extractTime(departureDateTime)}
          //onChangeText={text => onChangeDepartureTime(text)}
        />
      </View>
      {show && (
        <RNDateTimePicker
          testID="dateTimePicker"
          value={departureDateTime}
          mode={mode}
          is24Hour={true}
          display="default"
          onChange={onChange}
        />
      )}
    </View>
  );
};

function navigateToPageTwo(props, dep, arr, depDateTime, lstDep, lstArr) {
  /* Why are they shown in reverse order?
    Alert.alert("Departure station is : " + dep)
    Alert.alert("Arrival station is : " + arr)
    Alert.alert("Departure date is : " + depDate)
    Alert.alert("Departure time is : " + depTime)*/

  /*lstDep.forEach( (station) => {
        console.log("Station name : " + station["name"])
    })*/

  //check departure and arrival stations empty

  if (dep === '') {
    Alert.alert('Departure Station is empty');
    return;
  }

  if (arr === '') {
    Alert.alert('Arrival Station is empty');
    return;
  }

  //check departure and arrival stations correct

  //I filter the list of Departure station
  //mantaining the item with name equal to the
  //selected station. If the name inserted by
  //the users is exactly one item of the list
  //length is equal to 1, otherwise to 0
  const depIsInLstDep = lstDep.includes(dep);
  const arrIsInLstArr = lstArr.includes(arr);
  console.log('Dep correct : ' + depIsInLstDep);
  console.log('Arr correct : ' + arrIsInLstArr);

  //console.log("Dep correct : " + depIsInLstDep)
  //console.log("Arr correct : " + arrIsInLstArr)
  //console.log("Type of lstDep : " + typeof(lstDep))

  if (!depIsInLstDep) {
    Alert.alert('Departure Station is not correct');
    return;
  }

  if (!arrIsInLstArr) {
    Alert.alert('Arrival Station is not correct');
    return;
  }

  //check date and time empty

  /*const now = new Date();

    if(depDate === ""){ //case empty date
        depDate = now.getDate() + "/" + (now.getMonth()+1)  + "/" + now.getFullYear();
    }

    if(depTime === ""){ //case empty time
        depTime = now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
    }*/

  props.navigation.navigate(pageTwoName, {
    departureStation: dep,
    arrivalStation: arr,
    departureDate: extractDateForUrl(depDateTime),
    departureTime: extractTime(depDateTime),
  });
}

//It is not possible to retrieve directly all the possible station. I have to get stations whose names contains a certain
//text inserted by the user
const getStationsFromApi = (stationName, query) =>
  fetch(
    'http://transport.opendata.ch/v1/locations?type=station&query=' +
      encodeURI(stationName),
  )
    .then(response => response.json())
    .then(json => {
      let data = json.stations;
      data = data.filter(function(item) {
        return item.icon === 'train';
      });
      data = data.map(v => v.name);
      if (query === 'd') {
        global.departure = data;
        return global.departure;
      } else {
        global.arrival = data;
        return global.arrival;
      }
    });

const pageOneName = 'searchRoute';
const pageTwoName = 'routeResults';
const pageThreeName = 'routeDetails';

const SearchScreen = props => {
  const [selectedDeparture, onChangeSelectedDeparture] = React.useState('');
  React.useEffect(() => {
    console.log('departure ' + selectedDeparture);
    getStationsFromApi(selectedDeparture, 'd');
  }, [selectedDeparture]);
  const [selectedArrival, onChangeSelectedArrival] = React.useState('');
  React.useEffect(() => {
    console.log('arrival ' + selectedArrival);
    getStationsFromApi(selectedArrival, 'a');
  }, [selectedArrival]);
  const [departureDateTime, onChangeDepartureDateTime] = React.useState(
    new Date(),
  );

  //retrieval of departure stations from remote
  const [lstDepartureStations, setLstDepartureStations] = React.useState([]);
  React.useEffect(() => {
    getStationsFromApi(selectedDeparture, setLstDepartureStations);
  }, [selectedDeparture]);

  //retrieval of arrival stations from remote
  const [lstArrivalStations, setLstArrivalStations] = React.useState([]);
  React.useEffect(() => {
    getStationsFromApi(selectedArrival, setLstArrivalStations);
  }, [selectedArrival]);

  // Attention! If console.log is written here. It is called each time the components is recreated.
  // console.log("Before fetch : " + stations)

  return (
    <SafeAreaView>
      <ScrollView>
        <KeyboardAwareScrollView
          resetScrollToCoords={{x: 0, y: 0}}
          contentContainerStyle={styles.container}
          scrollEnabled={false}>
          <Text />
          <Text />
          <Image style={styles.image} source={require('./assets/train.png')} />
          <Text />
          <Text />
          <Text />
          <Text style={styles.titleStyle}>DEPARTURE</Text>
          <Autocomplete
            autoCapitalize="sentences"
            autoCorrect={false}
            containerStyle={styles.autocompleteContainer}
            data={
              selectedDeparture.length === 0 ||
              global.departure.includes(selectedDeparture)
                ? []
                : global.departure
            }
            onChangeText={onChangeSelectedDeparture}
            placeholder="Lugano"
            keyExtractor={(item, index) => index.toString()}
          />
          <Text />
          <Text />
          <Text style={styles.titleStyle}>ARRIVAL</Text>
          <Autocomplete
            autoCapitalize="none"
            autoCorrect={false}
            containerStyle={styles.autocompleteContainer}
            data={
              selectedArrival.length === 0 ||
              global.arrival.includes(selectedArrival)
                ? []
                : global.arrival
            }
            onChangeText={onChangeSelectedArrival}
            placeholder="Bern"
            keyExtractor={(item, index) => index.toString()}
          />
          <Text />
          <Text />
          <DateTimeInput
            departureDateTime={departureDateTime}
            onChangeDepartureDateTime={onChangeDepartureDateTime}
          />
          <Text />
          <Text />
          <Button
            title="Find routes"
            onPress={navigateToPageTwo.bind(
              this,
              props,
              selectedDeparture,
              selectedArrival,
              departureDateTime,
              global.departure,
              global.arrival,
            )}
            style={styles.btnFindRouteStyle}
            color="#004d40"
          />
        </KeyboardAwareScrollView>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    //backgroundColor: 'red',
    flex: 1,
    margin: 16,
  },

  image: {
    justifyContent: 'center',
    alignItems: 'center',
  },

  titleStyle: {
    fontSize: 24,
  },

  textInputStyle: {
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    marginBottom: 16,
    fontSize: 18,
  },

  dateAndTimeViewStyle: {
    flexDirection: 'row',
    marginBottom: 16,
    //backgroundColor: 'green',
  },

  dateOrTimeViewStyle: {
    flex: 1,
  },

  btnFindRouteStyle: {
    //flex: 1,
    color: '#eceff1',
  },
});

export default SearchScreen;
