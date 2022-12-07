#!/usr/bin/python
# https://github.com/google/python-fire/blob/master/docs/guide.md#accessing-properties
# python accessing-properties.py --code=JFK code
# python accessing-properties.py --code=SJC name
# pytone accessing-properties.py --code=ALB city


from airports import airports
import fire

class Airport(object):
    def __init__(self, code):
        self.code = code
        self.name = dict(airports).get(self.code)
        self.city = self.name.split(',')[0] if self.name else None

if __name__ == "__main__":
    fire.Fire(Airport)