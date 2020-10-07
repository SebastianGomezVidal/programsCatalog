import React, {Component, useEffect, useState} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  FlatList,
  ActivityIndicator,
} from 'react-native';
import {useRoute} from '@react-navigation/native';
import Moment from 'moment';

function pad(num) {
  return ('0' + num).slice(-2);
}
function getTimeFromDate(timestamp) {
  /*var date = new Date(timestamp * 1000);
  var hours = date.getHours();
  var minutes = date.getMinutes();
  var seconds = date.getSeconds();
  return pad(hours) + ':' + pad(minutes) + ':' + pad(seconds);*/
  return Moment(timestamp).parseZone(timestamp).format('h:mm:ss a')
}
const RouteDetailsScreen = props => {
  const {connection} = props.route.params;
  const [sections, setSections] = useState(connection.sections);
  const [departureCity, setDepartureCity] = useState(
    connection.from.station.name,
  );
  const [arrivalCity, setArrivalCity] = useState(connection.to.station.name);
  const [departureTime, setDepartureTime] = useState(
    getTimeFromDate(connection.from.departure),
  );
  const [arrivalTime, setArrivalTime] = useState(
    getTimeFromDate(connection.to.arrival),
  );

  return (
    <View style={styles.globalViewStyle}>
      <View style={{backgroundColor: '#004D40', marginBottom: 10, flex: 0}}>
        <Text style={styles.titleStyle}>
          {departureCity} to {arrivalCity}
        </Text>
        <Text style={styles.textStyle}>
          {departureTime} - {arrivalTime}
        </Text>
      </View>

      <FlatList
        style={styles.journeyDetailsStyle}
        data={sections}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({item}) => (
          <View>
            <FlatList
              data={item.journey == null ? [] : item.journey.passList}
              keyExtractor={(item, index) => index.toString()}
              renderItem={({item}) => (
                <View style={styles.genericViewStyle}>
                  <Text style={{fontSize: 15, fontWeight: 'bold'}}>
                    {item.location.name}
                  </Text>
                  {/*{getTimeFromDate(item.arrivalTimestamp) != '01:00:00' && (*/}
                  {item.arrival != null && (
                    <Text>
                      Arrival: {getTimeFromDate(item.arrival)}
                    </Text>
                  )}
                  {/*{getTimeFromDate(item.departureTimestamp) != '01:00:00' && (*/}
                  {item.departure != null && (
                    <Text>
                      Departure: {getTimeFromDate(item.departure)}
                    </Text>
                  )}
                </View>
              )}
            />
          </View>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  globalViewStyle: {
    //backgroundColor: 'red',
    flex: 1,
    margin: 16,
  },

  genericViewStyle: {
    backgroundColor: 'white',
    margin: 5,
  },

  journeyDetailsStyle: {
    backgroundColor: 'white',
    borderWidth: 2,
    borderColor: 'white',
    marginBottom: 10,
    flex: 3,
  },

  titleStyle: {
    height: 30,
    marginVertical: 4,
    marginLeft: 10,
    fontSize: 25,
    color: '#eceff1',
    fontWeight: 'bold',
  },

  textStyle: {
    height: 30,
    marginBottom: 4,
    marginLeft: 10,
    fontSize: 18,
    color: '#eceff1',
    fontWeight: 'bold',
  },
});

export default RouteDetailsScreen;
