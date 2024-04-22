import React, { useState } from "react";
import {
  Button,
  StyleSheet,
  TextInput,
  NativeModules,
  SafeAreaView,
  View,
} from "react-native";
const { NFCModule } = NativeModules;
import DataView from "./DataView";

export default function App() {
  const [mrzKey, setMrzKey] = useState("");
  const [data, setData] = useState(undefined);

  const handlePress = () => {
    // const mrzKey = "755215635790032222706225";
    NFCModule.read(mrzKey)
      .then((response) => {
        const _data = JSON.parse(response);
        setData(_data);
      })
      .catch((error) => {
        console.error("Error:", error);
        setData(undefined);
      });
  };
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.vStack}>
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.input}
            value={mrzKey}
            onChangeText={setMrzKey}
            placeholder="Enter MRZ Key"
            autoCapitalize="none"
            autoCorrect={false}
          />
        </View>
        <Button title="Read NFC" onPress={handlePress} />

        <View style={styles.dataView}>
          <DataView data={data} />
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "flex-start",
    alignItems: "center",
  },
  vStack: {
    flex: 1,
    width: "100%",
  },
  dataView: {
    flex: 1,
  },
  inputContainer: {
    flexDirection: "row",
    height: 40,
    padding: 10,
    borderWidth: 1,
    borderRadius: 10,
    margin: 20,
  },
  input: {
    flex: 1,
    marginHorizontal: 5,
  },
});
