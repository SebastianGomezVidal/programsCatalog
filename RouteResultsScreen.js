import React, {Component} from 'react';
import {View, FlatList, Text, StyleSheet, TouchableOpacity} from 'react-native';
import axios from 'axios';
import {Card} from 'react-native-elements';
import Moment from 'moment';

const pageOneName = 'searchRoute';
const pageTwoName = 'routeResults';
const pageThreeName = 'routeDetails';

export default class RouteResultsScreen extends Component {
  constructor(props) {
    super(props);

    this.navigation = props.navigation;
    this.depStation = props.route.params.departureStation;
    this.arrStation = props.route.params.arrivalStation;
    this.depDate = props.route.params.departureDate; //format 17/6/2020(day/month/year)
    this.depTime = props.route.params.departureTime; //format 15:34:25(hours:min:sec)

    //console.log("Dep : " + this.depStation)
    //console.log("Arr : " + this.arrStation)
    //console.log("DepDate : " + this.depDate)
    //console.log("DepTime : " + this.depTime)

    this.state = {
      dataSource: [],
      isLoading: true,
    };
  }

  renderItem = ({item}) => {
    //console.log("Departure date : " + item.from.departure)
    //YYYY -> year in four digits(i.e. 2020)
    //MMM -> months in three letter (i.e. Jun, Jul, Aug)
    //D -> day in number from 1 to 31
    //console.log("Departure date in moment format : " + Moment(item.from.departure).format('YYYY MMM D'))
    //h -> hour in 12 hours format
    //mm -> minutes(0..59)
    //ss -> seconds(0..59)
    //a -> "pm" or "am"

    //Moment(item.from.departure) -> convert the received time and date in UTC
    //.parseZone(item.from.departure) -> convert in time zone of item.from.departure
    const departureDateInLocalTimeZone = Moment(item.from.departure).parseZone(
      item.from.departure,
    );
    const arrivalDateInLocalTimeZone = Moment(item.to.arrival).parseZone(
      item.to.arrival,
    );

    //console.log("Departure time : " + item.from.departure)
    //console.log("Departure time in moment format : " + departureDateInLocalTimeZone.format('h:mm:ss a'))

    //console.log("Item : " + JSON.stringify(item))

    //item.transfers contains the number of intermediate stops the user should do
    //delete item.transfers

    return (
      <View>
        <TouchableOpacity
          onPress={() => {
            this.navigation.navigate(pageThreeName, {
              connection: item,
            });
            //Alert.alert("Route at hour " + departureDateInLocalTimeZone.format('h:mm:ss a') + " clicked")
          }}>
          <Card>
            <Text style={styles.title}>
              Route: From {item.from.station.name} To {item.to.station.name}{' '}
            </Text>
            <Text style={styles.paragraph}>
              {' '}
              Date: {departureDateInLocalTimeZone.format('YYYY MMM D')}{' '}
            </Text>
            <Text style={styles.paragraph}>
              {' '}
              Start time: {departureDateInLocalTimeZone.format(
                'h:mm:ss a',
              )}{' '}
            </Text>
            <Text style={styles.paragraph}>
              {' '}
              Arrival time: {arrivalDateInLocalTimeZone.format(
                'h:mm:ss a',
              )}{' '}
            </Text>
            <Text style={styles.paragraph}>
              {' '}
              Intermediate stops: {item.transfers}{' '}
            </Text>
            <Text style={styles.paragraph}>
              {' '}
              Platform: {item.from.platform}{' '}
            </Text>
          </Card>
        </TouchableOpacity>
      </View>
    );
  };

  componentDidMount() {
    const url =
      'http://transport.opendata.ch/v1/connections?from=' +
      encodeURI(this.depStation) +
      '&to=' +
      encodeURI(this.arrStation) +
      '&date=' +
      this.depDate +
      '&time=' +
      this.depTime +
      '&limit=10';

    console.log('Url:' + url);

    axios
      .get(url)
      .then(response => {
        // Success 🎉
        /*console.log('HERE');
                console.log(response.data);
                console.log('HERE1');
                console.log(response.status);
                console.log('HERE2');
                console.log(response.headers);
                console.log("Data lenght : " + response.data.connections.length)*/
        this.setState({
          dataSource: response.data.connections,
        });
      })
      .catch(error => {
        console.error(error);
      });
  }

  render() {
    //console.log("Render - Data lenght : " + this.state.dataSource.length)
    return (
      <View>
        <FlatList
          data={this.state.dataSource}
          renderItem={(item, index) => this.renderItem(item, index)}
          keyExtractor={(item, index) => index.toString()}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  title: {
    //margin: 24,
    fontSize: 16,
    fontWeight: 'bold',
    //textAlign: 'center',
    color: '#eceff1',
    backgroundColor: '#004d40',
  },
});
