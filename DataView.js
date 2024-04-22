import React from "react";
import { View, Text, ScrollView, StyleSheet } from "react-native";

const DataView = ({ data }) => {
  console.log(data);
  const renderDataItem = (key, value) => (
    <View style={styles.item} key={key}>
      <Text style={styles.key}>{key}:</Text>
      <Text numberOfLines={1} ellipsizeMode="middle" style={styles.value}>
        {value}
      </Text>
    </View>
  );

  return data ? (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>DG1 Data</Text>
      {Object.entries(data.DG1).map(([key, value]) =>
        renderDataItem(key, value)
      )}
      <Text style={styles.header}>SOD</Text>
      <Text numberOfLines={1} ellipsizeMode="middle" style={styles.value}>
        {data.sod}
      </Text>
    </ScrollView>
  ) : null;
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 10,
  },
  header: {
    fontSize: 18,
    fontWeight: "bold",
    marginTop: 20,
    marginBottom: 10,
  },
  item: {
    flexDirection: "row",
    marginBottom: 5,
  },
  key: {
    fontWeight: "bold",
    marginRight: 5,
  },
  value: {
    flex: 1,
    flexWrap: "nowrap",
  },
});

export default DataView;
