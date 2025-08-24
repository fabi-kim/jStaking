package com.springboot.staking.infrastructure.adaptor.proxy.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CosmosTxResponse(
    Tx tx,
    TxResponse tx_response
) {

  public record Tx(
      Body body,
      AuthInfo auth_info,
      List<String> signatures
  ) {

    public record Body(
        List<Message> messages,
        String memo,
        String timeout_height,
        List<Object> extension_options,
        List<Object> non_critical_extension_options
    ) {

      public record Message(
          @JsonProperty("@type")
          String type,
          String delegator_address,
          String validator_address,
          Amount amount
      ) {

        public record Amount(String denom, String amount) {

        }
      }
    }

    public record AuthInfo(
        List<SignerInfo> signer_infos,
        Fee fee,
        Object tip
    ) {

      public record SignerInfo(
          PublicKey public_key,
          ModeInfo mode_info,
          String sequence
      ) {

        public record PublicKey(String type, String key) {

        }

        public record ModeInfo(Single single) {

          public record Single(String mode) {

          }
        }
      }

      public record Fee(
          List<Amount> amount,
          String gas_limit,
          String payer,
          String granter
      ) {

        public record Amount(String denom, String amount) {

        }
      }
    }
  }

  public record TxResponse(
      String height,
      String txhash,
      String codespace,
      int code,
      String data,
      String raw_log,
      List<Log> logs,
      String info,
      String gas_wanted,
      String gas_used,
      Tx tx,
      String timestamp,
      List<Event> events
  ) {

    public record Log(
        int msg_index,
        String log,
        List<Event> events
    ) {

    }

    public record Event(
        String type,
        List<Attribute> attributes
    ) {

      public record Attribute(String key, String value, Boolean index) {

      }
    }
  }
}