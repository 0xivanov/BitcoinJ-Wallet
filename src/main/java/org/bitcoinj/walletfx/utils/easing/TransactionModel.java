package org.bitcoinj.walletfx.utils.easing;

import lombok.Data;

import java.util.List;

@Data
public class TransactionModel {
    private List<Input> inputs;
}
