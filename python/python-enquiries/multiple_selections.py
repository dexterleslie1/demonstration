import enquiries

options = ['Thing 1', 'Thing 2', 'Thing 3']
response = enquiries.choose('Pick some things', options, multi=True)

print('You chose "{}"'.format(response))