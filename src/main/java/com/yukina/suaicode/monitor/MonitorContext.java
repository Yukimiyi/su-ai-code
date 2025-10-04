package com.yukina.suaicode.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitorContext implements Serializable {

    private String userId;

    private String appId;


    private static final long serialVersionUID = 4612065121864525453L;
}
